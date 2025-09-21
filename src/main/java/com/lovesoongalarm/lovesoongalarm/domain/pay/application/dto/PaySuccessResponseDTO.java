package com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaySuccessResponseDTO {
    private final String sessionId;
    private final String status;
    private final Long totalAmount;
}
