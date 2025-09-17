package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record MatchingResultDTO(
        int matchCount,
        String zone,
        List<NearbyUserMatchDTO> nearbyUsers
) {
    @Builder
    public record NearbyUserMatchDTO(
            Long userId,
            boolean isMatching
    ) {
    }
}
