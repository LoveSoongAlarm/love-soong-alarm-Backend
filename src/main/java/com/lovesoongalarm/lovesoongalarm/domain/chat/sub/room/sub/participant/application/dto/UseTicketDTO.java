package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class UseTicketDTO {

    @Schema(name = "UseTicketRequest")
    public record Request(
            Long chatRoomId
    ) {
    }

    @Schema(name = "UseTicketResponse")
    @Builder
    public record Response(
            Boolean ticketUsed,
            Integer remainingChatTicket
    ) {
    }
}
