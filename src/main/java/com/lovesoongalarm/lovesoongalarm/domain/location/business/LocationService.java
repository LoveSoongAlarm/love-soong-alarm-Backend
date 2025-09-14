package com.lovesoongalarm.lovesoongalarm.domain.location.business;

import org.springframework.stereotype.Service;

import java.util.List;

public interface LocationService {
    void updateLocation(Long userId, double latitude, double longitude);

    List<Long> findNearby(Long userId);
}
