package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.converter.UserConverter;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.*;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.implement.InterestSaver;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.ELabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.sub.hashtag.persistence.entity.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_GENDER_KEY;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_INTEREST_KEY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRetriever userRetriever;
    private final InterestSaver interestSaver;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserConverter userConverter;

    public String getUserNickname(Long userId) {
        User findUser = userRetriever.findByIdAndOnlyActive(userId);
        return findUser.getNickname();
    }

    @Transactional
    public Void onBoardingUser(Long userId, OnBoardingRequestDTO request){
        User findUser = userRetriever.findByIdAndOnlyInActive(userId);
        findUser.updateFromOnboardingAndProfile(request.nickname(), request.major(), request.birthDate(), EGender.valueOf(request.gender()), request.emoji());

        List<Interest> interests = request.interests().stream()
                .map(interestDto -> {
                    Interest interest = Interest.create(
                            ELabel.valueOf(interestDto.label()),
                            findUser,
                            EDetailLabel.valueOf(interestDto.detailLabel())
                    );

                    // Hashtag 생성 및 Interest에 연결
                    List<Hashtag> hashtags = interestDto.hashTags().stream()
                            .map(tag -> Hashtag.create(tag, interest))
                            .toList();

                    interest.addHashtags(hashtags);
                    return interest;
                })
                .toList();

        interestSaver.saveAll(interests);

        // Redis에 취향 정보 저장
        updateRedis(userId, EGender.valueOf(request.gender()), interests);
      
        return null;
    }

    public UserResponseDTO getUser(Long targetId){
        User findUser = userRetriever.findByIdAndOnlyActive(targetId);

        int age = calculateAge(findUser.getBirthDate());

        return UserResponseDTO.from(findUser, age);
    }

    public UserMeResponseDTO getMe(Long userId){
        User findUser = userRetriever.findByIdAndOnlyActive(userId);

        return UserMeResponseDTO.from(findUser);
    }

    @Transactional
    public Void updateUser(Long userId, UserUpdateRequestDTO request) {
        User findUser = userRetriever.findByIdAndOnlyActive(userId);
        findUser.updateFromOnboardingAndProfile(
                request.nickname(),
                request.major(),
                request.birthDate(),
                EGender.valueOf(request.gender()),
                request.emoji()
        );

        findUser.getInterests().clear();

        // 새로운 Interest 전부 추가
        for (InterestUpdateRequestDTO dto : request.interests()) {
            Interest newInterest = Interest.create(
                    ELabel.valueOf(dto.label()),
                    findUser,
                    EDetailLabel.valueOf(dto.detailLabel())
            );

            List<Hashtag> hashtags = dto.hashTags().stream()
                    .map(tag -> Hashtag.create(tag, newInterest))
                    .toList();

            findUser.getInterests().add(newInterest);
            newInterest.getHashtags().addAll(hashtags);
        }

        // Redis 업데이트
        updateRedis(userId, EGender.valueOf(request.gender()), findUser.getInterests());

        return null;
    }


    public UserSlotResponseDTO getUserSlots(Long userId) {
        User findUser = userRetriever.findByIdAndOnlyActive(userId);
        UserSlotResponseDTO slotInfo = userConverter.createSlotInfo(findUser);
        return slotInfo;
    }

    public UserTicketResponseDTO getUserTickets(Long userId) {
        User user = userRetriever.findByIdAndOnlyActive(userId);
        UserTicketResponseDTO ticketInfo = userConverter.createTicketInfo(user);
        return ticketInfo;
    }

    private int calculateAge(Integer birthDate){
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthDate + 1;

        if(age < 0){
            throw new CustomException(UserErrorCode.INVALID_USER_AGE);
        }
        return age;
    }

    private void updateRedis(Long userId, EGender gender, List<Interest> interests) {
        List<String> interestValues = interests.stream()
                .map(interest -> interest.getDetailLabel().name())
                .toList();

        stringRedisTemplate.delete(USER_INTEREST_KEY + userId);

        if (!interestValues.isEmpty()) {
            stringRedisTemplate.opsForSet().add(USER_INTEREST_KEY + userId, interestValues.toArray(new String[0]));
        }

        stringRedisTemplate.opsForHash().put(USER_GENDER_KEY, String.valueOf(userId), gender.name());
    }
}
