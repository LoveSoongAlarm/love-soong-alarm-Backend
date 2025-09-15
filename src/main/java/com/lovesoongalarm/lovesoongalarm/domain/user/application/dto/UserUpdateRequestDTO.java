package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UserUpdateRequestDTO(
        @NotEmpty(message = "닉네임은 널일 수 없습니다.")
        String nickname,
        String major,
        @NotNull(message = "연도는 널일 수 없습니다.")
        Integer birthDate,
        @NotEmpty(message = "성별은 널일 수 없습니다.")
        String gender,
        @NotEmpty(message = "이모지는 널일 수 없습니다.")
        String emoji,
        List<InterestUpdateRequestDTO> interests
) {
}
