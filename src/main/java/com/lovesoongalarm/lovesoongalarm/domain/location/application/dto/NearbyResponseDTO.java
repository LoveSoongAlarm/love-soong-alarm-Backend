package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NearbyResponseDTO(
        Integer matchCount,
        List<NearbyUserResponseDTO> nearbyUsersInformation) {
}
