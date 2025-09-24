package com.lovesoongalarm.lovesoongalarm.domain.notification.implement;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FCMTokenDeleter {

    private final FCMTokenRepository fcmTokenRepository;

    public void deleteByToken(String token) {
        fcmTokenRepository.deleteByToken(token);
        log.info("FCM 토큰 삭제 완료: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
    }
}
