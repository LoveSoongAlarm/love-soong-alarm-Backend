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

    public void updateAfterNewMessage(Long chatRoomId, Message message, Long partnerId) {
        log.info("새 메시지 후 채팅방 목록 업데이트 - chatRoomId: {}, partnerId: {}", chatRoomId, partnerId);

        try {
            UserChatUpdateDTO updateEvent = UserChatUpdateDTO.builder()
                    .chatRoomId(chatRoomId)
                    .lastMessageContent(message.getContent())
                    .timestamp(message.getCreatedAt())
                    .isMyMessage(false)
                    .isRead(message.isRead())
                    .build();

            userSubscriptionService.publishUserChatUpdate(partnerId, updateEvent);
            log.info("새 메시지 후 채팅방 목록 업데이트 완료 - partnerId: {}", partnerId);
        } catch (Exception e) {
            log.error("채팅방 목록 업데이트 실패 - chatRoomId: {}, partnerId: {}",
                    chatRoomId, partnerId, e);
        }
    }
}
