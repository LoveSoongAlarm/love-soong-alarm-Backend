package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.UnreadCountService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatListUpdateService {

    private final UserSubscriptionService userSubscriptionService;
    private final UnreadCountService unreadCountService;
    private final WebSocketNotificationSender webSocketNotificationSender;

    public void updateAfterNewMessage(Long chatRoomId, Message message, Long partnerId) {
        log.info("새 메시지 후 채팅방 목록 업데이트 - chatRoomId: {}, partnerId: {}", chatRoomId, partnerId);

        try {
            updatePartnerChatList(partnerId, chatRoomId, message, false);
            updateUnreadBadge(partnerId);
            log.info("새 메시지 후 채팅방 목록 업데이트 완료 - partnerId: {}", partnerId);
        } catch (Exception e) {
            log.error("채팅방 목록 업데이트 실패 - chatRoomId: {}, partnerId: {}",
                    chatRoomId, partnerId, e);
        }
    }

    private void updatePartnerChatList(Long partnerId, Long chatRoomId, Message message, boolean isMyMessage) {
        UserChatUpdateDTO updateEvent = UserChatUpdateDTO.builder()
                .chatRoomId(chatRoomId)
                .lastMessageContent(message.getContent())
                .timestamp(message.getCreatedAt())
                .isMyMessage(isMyMessage)
                .isRead(message.isRead())
                .build();

        userSubscriptionService.publishUserChatUpdate(partnerId, updateEvent);
        log.debug("상대방 채팅방 목록 업데이트 전송 - partnerId: {}, chatRoomId: {}", partnerId, chatRoomId);
    }

    private void updateUnreadBadge(Long userId) {
        int totalUnreadCount = unreadCountService.getTotalUnreadCount(userId);
        webSocketNotificationSender.sendUnreadBadgeUpdate(userId, totalUnreadCount);
        log.debug("안 읽은 메시지 배지 업데이트 - userId: {}, count: {}", userId, totalUnreadCount);
    }
}
