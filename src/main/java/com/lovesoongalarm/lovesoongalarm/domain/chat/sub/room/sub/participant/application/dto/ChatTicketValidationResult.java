package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto;

public record ChatTicketValidationResult(
        boolean canSend,
        Integer availableTickets
) {
    public static ChatTicketValidationResult success() {
        return new ChatTicketValidationResult(true, null);
    }

    public static ChatTicketValidationResult limitExceeded(int availableTickets, int limit) {
        return new ChatTicketValidationResult(
                false,
                availableTickets
        );
    }
}
