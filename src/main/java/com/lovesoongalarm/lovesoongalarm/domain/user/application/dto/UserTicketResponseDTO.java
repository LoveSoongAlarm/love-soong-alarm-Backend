package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import lombok.Builder;

@Builder
public record UserTicketResponseDTO(
        Integer chatTicket
) {
}
