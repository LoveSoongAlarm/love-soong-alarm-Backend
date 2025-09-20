package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageReadService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.ReadProcessingService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.RedisSubscriber;
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
    private final UnreadBadgeUpdateService unreadBadgeUpdateService;
    private final RedisSubscriber redisSubscriber;

    public void notifyNewMessage(Long chatRoomId, Message message, Long senderId) {
        log.info("새 메시지 알림 정책 결정 시작 - chatRoomId: {}, messageId: {}, senderId: {}",
                chatRoomId, message.getId(), senderId);

        try {
            User partner = userService.getPartnerUser(chatRoomId, senderId);
            Long partnerId = partner.getId();

            webSocketNotificationSender.sendMessageToUser(senderId, message, true);

            if (redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) {
                webSocketNotificationSender.sendMessageToUser(partnerId, message, false);
                MessageReadService.ReadResult readResult =
                        messageReadService.markSingleMessageAsRead(message.getId(), chatRoomId, partnerId);
                readProcessingService.handleMessageReceiveReadResult(readResult);
            }

            if (redisSubscriber.isChatListSubscribed(senderId)) {
                chatListUpdateService.updateAfterNewMessage(chatRoomId, message, partnerId);
            }

            unreadBadgeUpdateService.updateUnreadBadge(partnerId);

            log.info("새 메시지 알림 정책 결정 완료 - partnerId: {}", partnerId);
        } catch (Exception e) {
            log.error("메시지 알림 정책 결정 중 오류 - chatRoomId: {}, messageId: {}",
                    chatRoomId, message.getId(), e);
        }
    }
}
