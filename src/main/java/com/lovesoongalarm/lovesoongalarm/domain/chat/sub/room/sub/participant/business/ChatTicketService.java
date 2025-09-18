package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.exception.ChatRoomParticipantErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatTicketService {

    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;
    private final ChatRoomParticipantUpdater chatRoomParticipantUpdater;

    private final MessageSender messageSender;
    private final SessionService sessionService;

    private static final int FREE_MESSAGE_LIMIT = 10;

    @Transactional
    public void validateAndProcessMessage(Long userId, Long chatRoomId) {
        ChatRoomParticipant participant = chatRoomParticipantRetriever
                .findByUserIdAndChatRoomId(userId, chatRoomId);

        if (participant.hasUnlimitedChat()) return;

        WebSocketSession session = sessionService.getSession(userId);
        if (session == null || !session.isOpen()) {
            log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
            return;
        }

        if (participant.getFreeMessageCount() >= FREE_MESSAGE_LIMIT) {
            messageSender.sendMessageCountLimit(session);
            throw new CustomException(ChatRoomParticipantErrorCode.EXCEED_MESSAGE_LIMIT);
        }
        chatRoomParticipantUpdater.incrementFreeMessageCount(participant.getId());
    }
}
