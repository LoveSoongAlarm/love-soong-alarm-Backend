package com.lovesoongalarm.lovesoongalarm.auth.application.dto;

import lombok.Builder;

@Builder
public record ReissueTokenResponseDTO(
        String accessToken
) {
    public static ReissueTokenResponseDTO from(String accessToken){
        return ReissueTokenResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}
