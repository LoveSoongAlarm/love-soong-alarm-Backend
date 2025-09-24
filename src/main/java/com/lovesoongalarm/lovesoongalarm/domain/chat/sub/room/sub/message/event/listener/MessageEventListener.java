package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event.listener;

import com.lovesoongalarm.lovesoongalarm.domain.notification.business.FCMPushService;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.ChatMessageNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event.MessageSentEvent;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEventListener {

    private final ChatMessageNotificationService chatMessageNotificationService;
    private final FCMPushService fcmPushService;
    private final UserService userService;
    private final RedisSubscriber redisSubscriber;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSent(MessageSentEvent event) {
        log.info("트랜잭션 커밋 후 웹소켓 메시지 전송 시작 - chatRoomId: {}, messageId: {}",
                event.chatRoomId(), event.message().getId());

        try {
            chatMessageNotificationService.notifyNewMessage(
                    event.chatRoomId(),
                    event.message(),
                    event.senderId()
            );

            handleFCMPushNotification(event);

            log.info("웹소켓 메시지 전송 완료 - messageId: {}", event.message().getId());
        } catch (Exception e) {
            log.error("웹소켓 메시지 전송 실패 - chatRoomId: {}, messageId: {}",
                    event.chatRoomId(), event.message().getId(), e);
        }
    }

    private void handleFCMPushNotification(MessageSentEvent event) {
        try {
            User sender = userService.findUserOrElseThrow(event.senderId());
            User partner = userService.getPartnerUser(event.chatRoomId(), event.senderId());

            boolean isPartnerOnline = redisSubscriber.isUserSubscribed(event.chatRoomId(), partner.getId());
            if (isPartnerOnline) {
                log.info("상대방이 온라인이므로 FCM 푸시 전송하지 않음 - partnerId: {}, chatRoomId: {}", partner.getId(), event.chatRoomId());
                return;
            }

            fcmPushService.sendChatMessagePush(
                    partner.getId(),
                    sender.getNickname(),
                    sender.getEmoji() != null ? sender.getEmoji() : "💬",
                    event.message().getContent(),
                    event.chatRoomId(),
                    event.senderId()
            );

            log.info("FCM 푸시 알림 전송 완료 - partnerId: {}", partner.getId());

        } catch (Exception e) {
            log.error("FCM 푸시 알림 처리 실패 - chatRoomId: {}, senderId: {}",
                    event.chatRoomId(), event.senderId(), e);
        }
    }
}
