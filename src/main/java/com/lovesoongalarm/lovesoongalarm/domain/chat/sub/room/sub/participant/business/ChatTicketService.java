package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto.ChatTicketValidationResult;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatTicketService {

    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;
    private final ChatRoomParticipantUpdater chatRoomParticipantUpdater;

    private static final int FREE_MESSAGE_LIMIT = 10;

    @Transactional
    public ChatTicketValidationResult validateMessageSending(Long userId, Long chatRoomId) {
        ChatRoomParticipant participant = chatRoomParticipantRetriever
                .findByUserIdAndChatRoomId(userId, chatRoomId);

        if (participant.hasUnlimitedChat()) {
            return ChatTicketValidationResult.success();
        }

        if (participant.getFreeMessageCount() >= FREE_MESSAGE_LIMIT) {
            int remainingTickets = participant.getTicketCount();
            return ChatTicketValidationResult.limitExceeded(remainingTickets, FREE_MESSAGE_LIMIT);
        }

        chatRoomParticipantUpdater.incrementFreeMessageCount(participant.getId());
        return ChatTicketValidationResult.success();
    }
}
