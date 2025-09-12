package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRetriever userRetriever;

    public String getUserNickname(Long userId) {
        User user = userRetriever.findByIdOrElseThrow(userId);
        return user.getNickname();
    }

    @Transactional
    public Void onBoardingUser(Long userId, OnBoardingRequestDTO request){
        User findUser = userRetriever.findById(userId);
        findUser.updateFromOnboarding(request.nickname(), request.phoneNumber(), request.major(), request.birthDate(), EGender.valueOf(request.gender()), request.emoji());

        return null;
    }

}
