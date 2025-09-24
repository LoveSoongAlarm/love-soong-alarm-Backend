package com.lovesoongalarm.lovesoongalarm.domain.notification.implement;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.FCMToken;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository.FCMTokenRepository;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EDeviceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FCMTokenRetriever {

    private final FCMTokenRepository fcmTokenRepository;

    public List<FCMToken> findByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId);
    }

    public Optional<FCMToken> findByUserIdAndDeviceType(Long userId, EDeviceType deviceType) {
        return fcmTokenRepository.findByUserIdAndDeviceType(userId, deviceType);
    }

    public List<String> findTokensByUserIds(List<Long> userIds) {
        return fcmTokenRepository.findTokensByUserIds(userIds);
    }

    public boolean existsByToken(String token) {
        return fcmTokenRepository.existsByToken(token);
    }
}
