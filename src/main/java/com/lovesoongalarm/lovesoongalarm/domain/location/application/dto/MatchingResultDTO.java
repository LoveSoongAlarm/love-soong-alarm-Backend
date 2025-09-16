package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record MatchingResultDTO(
        List<Long> userIds,
        int matchCount,
        Map<Long, Long> userMatchCounts
) {
}
