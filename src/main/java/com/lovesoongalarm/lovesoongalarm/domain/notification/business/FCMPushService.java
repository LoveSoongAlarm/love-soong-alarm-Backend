package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.google.firebase.messaging.*;
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

    /**
     * 단일 사용자에게 푸시 알림 전송
     */
    public void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        log.info("사용자에게 푸시 알림 전송 시작 - userId: {}, title: {}", userId, title);

        List<FCMToken> tokens = fcmTokenRetriever.findByUserId(userId);

        if (tokens.isEmpty()) {
            log.warn("사용자 FCM 토큰이 없습니다 - userId: {}", userId);
            return;
        }

        tokens.forEach(fcmToken -> sendSingleMessage(fcmToken.getToken(), title, body, data, fcmToken.getDeviceType()));

        log.info("사용자 푸시 알림 전송 완료 - userId: {}, 전송된 토큰 수: {}", userId, tokens.size());
    }

    /**
     * 여러 사용자에게 배치 푸시 알림 전송
     */
    public void sendToMultipleUsers(List<Long> userIds, String title, String body, Map<String, String> data) {
        log.info("여러 사용자에게 푸시 알림 전송 시작 - 대상 사용자 수: {}", userIds.size());

        List<String> tokens = fcmTokenRetriever.findTokensByUserIds(userIds);

        if (tokens.isEmpty()) {
            log.warn("대상 사용자들의 FCM 토큰이 없습니다 - userIds: {}", userIds);
            return;
        }

        sendBatchMessage(tokens, title, body, data);
        log.info("여러 사용자 푸시 알림 전송 완료 - 전송된 토큰 수: {}", tokens.size());
    }

    public void sendChatMessagePush(Long receiverId, String senderName, String senderEmoji,
                                    String messageContent, Long chatRoomId, Long senderId) {
        String title = String.format("%s %s님의 메시지", senderEmoji, senderName);
        String body = truncateMessage(messageContent, 100);

        Map<String, String> data = Map.of(
                "type", "CHAT_MESSAGE",
                "chatRoomId", chatRoomId.toString(),
                "senderId", senderId.toString(),
                "senderNickname", senderName,
                "senderEmoji", senderEmoji
        );

        sendToUser(receiverId, title, body, data);
    }

    /**
     * 단일 토큰으로 메시지 전송
     */
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
     * 여러 토큰으로 배치 메시지 전송
     */
    private void sendBatchMessage(List<String> tokens, String title, String body, Map<String, String> data) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : new HashMap<>())
                    .setWebpushConfig(createWebpushConfig(title, body, EDeviceType.WEB))
                    .build();

            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("FCM 배치 메시지 전송 완료 - 성공: {}, 실패: {}",
                    response.getSuccessCount(), response.getFailureCount());

            // 실패한 토큰들 처리
            if (response.getFailureCount() > 0) {
                handleBatchFailures(response, tokens);
            }

        } catch (Exception e) {
            log.error("FCM 배치 메시지 전송 실패", e);
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
                        .setIcon("/icon-192x192.png")
                        .setBadge("/badge-72x72.png")
                        .setTag("love-soong-alarm-notification")
                        .setRequireInteraction(true)
                        .addAction(new WebpushNotification.Action("open", "열기", "/icon-open.png"))
                        .addAction(new WebpushNotification.Action("close", "닫기", "/icon-close.png"))
                        .build())
                .putHeader("TTL", "86400") // 24시간
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
     * 배치 전송 실패 처리
     */
    private void handleBatchFailures(BatchResponse response, List<String> tokens) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String failedToken = tokens.get(i);
                Exception exception = responses.get(i).getException();

                log.error("배치 전송 실패 토큰: {}, 에러: {}",
                        failedToken.substring(0, Math.min(failedToken.length(), 20)) + "...",
                        exception != null ? exception.getMessage() : "알 수 없는 오류");

                // 유효하지 않은 토큰이면 삭제
                if (exception instanceof FirebaseMessagingException) {
                    FirebaseMessagingException fme = (FirebaseMessagingException) exception;
                    String errorCode = fme.getMessagingErrorCode() != null ?
                            fme.getMessagingErrorCode().toString() : "UNKNOWN";

                    if (isInvalidToken(errorCode)) {
                        fcmTokenDeleter.deleteByToken(failedToken);
                    }
                }
            }
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
