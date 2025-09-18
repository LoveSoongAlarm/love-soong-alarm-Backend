package com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto;

import jakarta.validation.constraints.NotEmpty;

public record PayItemRequestDTO(
        @NotEmpty(message = "아이템은 널일 수 없습니다.")
        String item
) {
}
