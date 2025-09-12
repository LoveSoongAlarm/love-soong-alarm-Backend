package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.exception.UserErrorCode;
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
}
