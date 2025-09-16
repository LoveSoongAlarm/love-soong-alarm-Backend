package com.lovesoongalarm.lovesoongalarm.domain.location.business;

import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.MatchingResultDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyResponseDTO;

import java.util.List;

public interface LocationService {
    void updateLocation(Long userId, double latitude, double longitude);

    MatchingResultDTO findNearby(Long userId);
}
