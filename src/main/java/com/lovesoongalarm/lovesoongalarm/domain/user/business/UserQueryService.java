package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
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
        findUser.updateFromOnboarding(request.nickname(), request.phoneNumber(), request.major(), request.birthDate(), EGender.valueOf(request.gender()), request.emoji());

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

}
