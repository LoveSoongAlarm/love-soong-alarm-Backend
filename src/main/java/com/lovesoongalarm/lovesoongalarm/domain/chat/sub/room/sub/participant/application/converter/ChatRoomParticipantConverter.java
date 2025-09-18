package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto.UseTicketDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomParticipantConverter {

    public UseTicketDTO.Response toUseTicketResponse(ChatRoomParticipant savedParticipant) {
        return UseTicketDTO.Response.builder()
                .ticketUsed(savedParticipant.getTicketUsed())
                .remainingChatTicket(savedParticipant.getUser().getChatTicket())
                .build();
    }
}
