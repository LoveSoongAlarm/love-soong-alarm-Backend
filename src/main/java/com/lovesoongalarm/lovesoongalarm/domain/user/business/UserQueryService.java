package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.InterestUpdateRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserUpdateRequestDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRetriever userRetriever;
    private final InterestSaver interestSaver;

    public String getUserNickname(Long userId) {
        User user = userRetriever.findByIdOrElseThrow(userId);
        return user.getNickname();
    }

    @Transactional
    public Void onBoardingUser(Long userId, OnBoardingRequestDTO request){
        User findUser = userRetriever.findById(userId);
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
      
        return null;
    }

    public UserResponseDTO getUser(Long targetId){
        User findUser = userRetriever.findById(targetId);

        int age = calculateAge(findUser.getBirthDate());

        return UserResponseDTO.from(findUser, age);
    }

    @Transactional
    public Void updateUser(Long userId, UserUpdateRequestDTO request){
        User findUser = userRetriever.findById(userId);
        findUser.updateFromOnboardingAndProfile(request.nickname(), request.major(), request.birthDate(), EGender.valueOf(request.gender()), request.emoji());

        List<Interest> existingInterests = findUser.getInterests();
        List<InterestUpdateRequestDTO> newInterests = request.interests();

        for (int i = 0; i < existingInterests.size(); i++) {
            Interest interest = existingInterests.get(i);
            InterestUpdateRequestDTO dto = newInterests.get(i);

            // Interest 값 덮어쓰기
            interest.updateInterestFromProfile(
                    ELabel.valueOf(dto.label()),
                    EDetailLabel.valueOf(dto.detailLabel())
            );

            // Hashtags 값 덮어쓰기
            interest.getHashtags().clear();

            List<Hashtag> updatedTags = dto.hashTags().stream()
                    .map(tag -> Hashtag.create(tag, interest))
                    .toList();

            interest.getHashtags().addAll(updatedTags);
        }

        return null;
    }

    private int calculateAge(Integer birthDate){
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthDate + 1;

        if(age < 0){
            throw new CustomException(UserErrorCode.INVALID_USER_AGE);
        }
        return age;
    }

}
