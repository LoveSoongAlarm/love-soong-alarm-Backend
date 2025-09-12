package com.lovesoongalarm.lovesoongalarm.security.dto;

import lombok.Builder;

@Builder
public record JwtDTO(
        String accessToken,
        String refreshToken
) {
    public static JwtDTO of(String accessToken, String refreshToken){
        return JwtDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
