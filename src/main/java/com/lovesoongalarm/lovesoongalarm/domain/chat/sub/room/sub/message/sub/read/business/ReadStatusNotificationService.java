package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReadStatusNotificationService {

    private final SubscriptionService subscriptionService;
    private final ChatSessionService chatSessionService;
    private final MessageSender messageSender;

    public void notifyReadStatusUpdate(Long chatRoomId, Long readerId, Long partnerId, Long lastReadMessageId) {
        log.info("읽음 상태 알림 - chatRoomId: {}, readerId: {}, partnerId: {}, lastReadMessageId: {}",
                chatRoomId, readerId, partnerId, lastReadMessageId);

        if(!subscriptionService.isUserSubscribed(chatRoomId, partnerId)){
            return;
        }

        WebSocketSession partnerSession = chatSessionService.getSession(partnerId);

        WebSocketMessageDTO.MessageReadNotification messageReadNotification = WebSocketMessageDTO.MessageReadNotification.builder()
                .type(EWebSocketMessageType.MESSAGE_READ)
                .chatRoomId(chatRoomId)
                .lastReadMessageId(lastReadMessageId)
                .build();

        messageSender.sendMessage(partnerSession, messageReadNotification);

        log.info("읽음 상태 알림 완료 - lastReadMessageId: {}", lastReadMessageId);
    }
}
