package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record MatchingResultDTO(
        int matchCount,
        String zone,
        List<Long> userIds
) {}
