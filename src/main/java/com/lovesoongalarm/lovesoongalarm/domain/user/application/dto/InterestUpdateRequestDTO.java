package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InterestUpdateRequestDTO(
        @NotEmpty(message = "취향은 널일 수 없습니다.")
        String label,
        @NotEmpty(message = "자세한 취향은 널일 수 없습니다.")
        String detailLabel,
        List<String> hashTags
) {
}
