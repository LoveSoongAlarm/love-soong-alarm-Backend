package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageReadService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.ReadProcessingService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageNotificationService {

    private final WebSocketNotificationSender webSocketNotificationSender;
    private final ChatListUpdateService chatListUpdateService;
    private final UserService userService;
    private final MessageReadService messageReadService;
    private final ReadProcessingService readProcessingService;

    private final RedisSubscriber redisSubscriber;

    public void notifyNewMessage(Long chatRoomId, Message message, Long senderId) {
        log.info("새 메시지 알림 정책 결정 시작 - chatRoomId: {}, messageId: {}, senderId: {}",
                chatRoomId, message.getId(), senderId);

        try {
            User partner = userService.getPartnerUser(chatRoomId, senderId);
            Long partnerId = partner.getId();

            webSocketNotificationSender.sendMessageToUser(partnerId, message, false);
            webSocketNotificationSender.sendMessageToUser(senderId, message, true);

            if (redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) {
                MessageReadService.ReadResult readResult =
                        messageReadService.markSingleMessageAsRead(message.getId(), chatRoomId, partnerId);

                readProcessingService.handleMessageReceiveReadResult(readResult);
            }

            chatListUpdateService.updateAfterNewMessage(chatRoomId, message, senderId, partnerId);
            log.info("새 메시지 알림 정책 결정 완료 - partnerId: {}", partnerId);
        } catch (Exception e) {
            log.error("메시지 알림 정책 결정 중 오류 - chatRoomId: {}, messageId: {}",
                    chatRoomId, message.getId(), e);
        }
    }

    public void notifyReadStatusUpdate(Long chatRoomId, Long readerId, Long partnerId) {
        log.info("읽음 상태 알림 정책 결정 - chatRoomId: {}, readerId: {}, partnerId: {}",
                chatRoomId, readerId, partnerId);
        try {
            webSocketNotificationSender.sendReadNotification(partnerId, chatRoomId, readerId);
            chatListUpdateService.updateReadStatus(partnerId, chatRoomId);
        } catch (Exception e) {
            log.error("읽음 상태 알림 정책 결정 중 오류", e);
        }
    }
}
