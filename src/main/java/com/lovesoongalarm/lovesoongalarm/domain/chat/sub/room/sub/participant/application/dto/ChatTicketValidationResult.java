package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto;

public record ChatTicketValidationResult(
        boolean canSend,
        Integer remainingFreeMessages,
        Integer availableTickets,
        String reason
) {
    public static ChatTicketValidationResult success() {
        return new ChatTicketValidationResult(true, null, null, null);
    }

    public static ChatTicketValidationResult limitExceeded(int availableTickets, int limit) {
        return new ChatTicketValidationResult(
                false,
                0,
                availableTickets,
                String.format("무료 메시지 %d개 모두 사용했습니다", limit)
        );
    }
}
