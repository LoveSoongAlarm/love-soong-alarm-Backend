package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.google.firebase.messaging.*;
import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.FCMTokenDeleter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.FCMTokenRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.FCMToken;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EDeviceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMPushService {

    private final FirebaseMessaging firebaseMessaging;
    private final FCMTokenRetriever fcmTokenRetriever;
    private final FCMTokenDeleter fcmTokenDeleter;

    public void sendChatMessagePush(Long receiverId, String senderName, String senderEmoji,
                                    String messageContent, Long chatRoomId, Long senderId) {
        String title = String.format("%s %s님의 메시지", senderEmoji, senderName);
        String body = truncateMessage(messageContent, Constants.FCM_MESSAGE_MAX_LENGTH);

        Map<String, String> data = Map.of(
                "type", "CHAT_MESSAGE",
                "chatRoomId", chatRoomId.toString(),
                "senderId", senderId.toString(),
                "senderNickname", senderName,
                "senderEmoji", senderEmoji
        );

        sendToUser(receiverId, title, body, data);
    }

    public void sendMatchingPush(Long userId, String message, Long matchingUserId, Long notificationId) {
        String title = "띠링~ 💝";
        String body = message;

        Map<String, String> data = Map.of(
                "type", "MATCHING",
                "notificationId", notificationId.toString(),
                "matchingUserId", matchingUserId.toString(),
                "timestamp", String.valueOf(System.currentTimeMillis())
        );

        sendToUser(userId, title, body, data);
        log.info("매칭 푸시 알림 전송 완료 - userId: {}, matchingUserId: {}", userId, matchingUserId);
    }

    private void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        log.info("사용자에게 푸시 알림 전송 시작 - userId: {}, title: {}", userId, title);

        List<FCMToken> tokens = fcmTokenRetriever.findByUserId(userId);

        if (tokens.isEmpty()) {
            log.warn("사용자 FCM 토큰이 없습니다 - userId: {}", userId);
            return;
        }

        tokens.forEach(fcmToken -> sendSingleMessage(fcmToken.getToken(), title, body, data, fcmToken.getDeviceType()));

        log.info("사용자 푸시 알림 전송 완료 - userId: {}, 전송된 토큰 수: {}", userId, tokens.size());
    }

    private void sendSingleMessage(String token, String title, String body, Map<String, String> data, EDeviceType deviceType) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : new HashMap<>())
                    .setWebpushConfig(createWebpushConfig(title, body, deviceType))
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("FCM 단일 메시지 전송 성공 - response: {}", response);

        } catch (FirebaseMessagingException e) {
            handleFirebaseException(e, token);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패 - token: {}",
                    token.substring(0, Math.min(token.length(), 20)) + "...", e);
        }
    }

    /**
     * 웹용 푸시 설정 생성
     */
    private WebpushConfig createWebpushConfig(String title, String body, EDeviceType deviceType) {
        if (deviceType != EDeviceType.WEB) {
            return null;
        }

        return WebpushConfig.builder()
                .setNotification(WebpushNotification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setIcon(Constants.FCM_NOTIFICATION_ICON)
                        .setTag(Constants.FCM_NOTIFICATION_TAG)
                        .setRequireInteraction(true)
                        .addAction(new WebpushNotification.Action("open", "열기", "/icon-open.png"))
                        .addAction(new WebpushNotification.Action("close", "닫기", "/icon-close.png"))
                        .build())
                .putHeader("TTL", String.valueOf(Constants.FCM_TTL_SECONDS))
                .build();
    }

    /**
     * Firebase 예외 처리
     */
    private void handleFirebaseException(FirebaseMessagingException e, String token) {
        String errorCode = e.getMessagingErrorCode() != null ?
                e.getMessagingErrorCode().toString() : "UNKNOWN";

        log.error("FCM 전송 실패 - 에러코드: {}, 토큰: {}, 메시지: {}",
                errorCode, token.substring(0, Math.min(token.length(), 20)) + "...", e.getMessage());

        // 유효하지 않은 토큰은 삭제
        if (isInvalidToken(errorCode)) {
            fcmTokenDeleter.deleteByToken(token);
            log.info("유효하지 않은 FCM 토큰 삭제: {}",
                    token.substring(0, Math.min(token.length(), 20)) + "...");
        }
    }

    /**
     * 토큰이 유효하지 않은지 판단
     */
    private boolean isInvalidToken(String errorCode) {
        return "UNREGISTERED".equals(errorCode) ||
                "INVALID_ARGUMENT".equals(errorCode) ||
                "REGISTRATION_TOKEN_NOT_REGISTERED".equals(errorCode);
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength) + "...";
    }

}
