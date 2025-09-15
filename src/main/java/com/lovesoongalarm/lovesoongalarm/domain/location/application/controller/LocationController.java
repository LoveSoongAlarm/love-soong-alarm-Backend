package com.lovesoongalarm.lovesoongalarm.domain.location.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.LocationRequest;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.facade.LocationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationFacade locationFacade;

    @PostMapping("/update")
    public BaseResponse<Void> update(
            @UserId Long userId,
            @RequestBody LocationRequest locationRequest
    ) {
        locationFacade.updateLocation(userId, locationRequest.latitude(), locationRequest.longitude());
        return BaseResponse.success(null);
    }

    @GetMapping("/nearby")
    public BaseResponse<NearbyResponseDTO> nearby(
            @UserId Long userId
    ) {
        return BaseResponse.success(locationFacade.findNearby(userId));
    }
}
