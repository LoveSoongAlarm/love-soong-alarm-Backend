package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.FCMTokenRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.FCMTokenDeleter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.FCMTokenRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.FCMTokenSaver;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.FCMToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FCMTokenService {

    private final FCMTokenSaver fcmTokenSaver;
    private final FCMTokenRetriever fcmTokenRetriever;
    private final FCMTokenDeleter fcmTokenDeleter;

    public void registerToken(Long userId, FCMTokenRequestDTO request) {
        log.info("FCM 토큰 등록 시작 - userId: {}, deviceType: {}", userId, request.deviceType());

        // 기존 토큰 확인 후 업데이트 또는 새로 생성
        Optional<FCMToken> existingToken = fcmTokenRetriever
                .findByUserIdAndDeviceType(userId, request.deviceType());

        if (existingToken.isPresent()) {
            // 기존 토큰 업데이트
            FCMToken token = existingToken.get();
            token.updateToken(request.fcmToken());
            fcmTokenSaver.save(token);
            log.info("FCM 토큰 업데이트 완료 - userId: {}, deviceType: {}", userId, request.deviceType());
        } else {
            // 새 토큰 생성
            FCMToken newToken = FCMToken.builder()
                    .userId(userId)
                    .token(request.fcmToken())
                    .deviceType(request.deviceType())
                    .build();
            fcmTokenSaver.save(newToken);
            log.info("새 FCM 토큰 등록 완료 - userId: {}, deviceType: {}", userId, request.deviceType());
        }
    }

    public void deleteToken(String token) {
        if (!fcmTokenRetriever.existsByToken(token)) {
            log.warn("삭제하려는 FCM 토큰이 존재하지 않음: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            return;
        }

        fcmTokenDeleter.deleteByToken(token);
        log.info("FCM 토큰 삭제 완료: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
    }
}
