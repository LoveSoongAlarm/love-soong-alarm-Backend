package com.lovesoongalarm.lovesoongalarm.domain.user.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRetriever {

    private final UserRepository userRepository;

    public User findByIdOrElseThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    public boolean existsById(Long targetUserId) {
        return userRepository.existsById(targetUserId);
    }

    public User findPartnerByChatRoomIdAndUserId(Long roomId, Long userId) {
        return userRepository.findPartnerByChatRoomIdAndUserId(roomId, userId);
    }

    public User findByIdAndOnlyActive(Long userId){
        return userRepository.findByIdAndStatus(userId, EUserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_ONBOARDING));
    }

    public User findByIdAndOnlyInActive(Long userId){
        return userRepository.findByIdAndStatus(userId, EUserStatus.INACTIVE)
                .orElseThrow(() -> new CustomException(UserErrorCode.ALREADY_ONBOARDING_USER));
    }
}
