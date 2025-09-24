package com.lovesoongalarm.lovesoongalarm.domain.notification.implement;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.FCMToken;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FCMTokenSaver {

    private final FCMTokenRepository fcmTokenRepository;

    public FCMToken save(FCMToken fcmToken) {
        return fcmTokenRepository.save(fcmToken);
    }
}
