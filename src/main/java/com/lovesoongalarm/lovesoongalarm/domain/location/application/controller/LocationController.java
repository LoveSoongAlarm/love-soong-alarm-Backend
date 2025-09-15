package com.lovesoongalarm.lovesoongalarm.domain.location.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.LocationRequest;
import com.lovesoongalarm.lovesoongalarm.domain.location.business.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/update")
    public BaseResponse<Void> update(
            @RequestParam Long userId,
            @RequestBody LocationRequest locationRequest
    ) {
        locationService.updateLocation(userId, locationRequest.latitude(), locationRequest.longitude());
        return BaseResponse.success(null);
    }

    @GetMapping("/nearby")
    public BaseResponse<List<Long>> nearby(
            @RequestParam Long userId
    ) {
        return BaseResponse.success(locationService.findNearby(userId));
    }
}
