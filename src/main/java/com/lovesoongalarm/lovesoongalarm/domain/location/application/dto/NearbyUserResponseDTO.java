package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserInterestResponseDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record NearbyUserResponseDTO(
        String name,
        Integer age,
        String major,
        String emoji,
        List<UserInterestResponseDTO> interests,
        String lastSeen,
        boolean isMatching,
        Double latitude,
        Double longitude
) {
}