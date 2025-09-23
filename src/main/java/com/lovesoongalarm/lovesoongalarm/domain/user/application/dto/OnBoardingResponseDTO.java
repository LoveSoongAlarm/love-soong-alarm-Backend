package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import lombok.Builder;

@Builder
public record OnBoardingResponseDTO(
        Long userId
) {
    public static OnBoardingResponseDTO from(Long userId){
        return OnBoardingResponseDTO.builder()
                .userId(userId)
                .build();
    }
}
