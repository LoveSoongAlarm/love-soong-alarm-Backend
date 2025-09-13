package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record OnBoardingRequestDTO(
        @NotEmpty(message = "닉네임은 널일 수 없습니다.")
        String nickname,
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phoneNumber,
        String major,
        @NotNull(message = "연도는 널일 수 없습니다.")
        Integer birthDate,
        @NotEmpty(message = "성별은 널일 수 없습니다.")
        String gender,
        @NotEmpty(message = "이모지는 널일 수 없습니다.")
        String emoji,
        List<OnBoardingInterestRequestDTO> interests
) {
}
