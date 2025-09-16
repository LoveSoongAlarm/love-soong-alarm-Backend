package com.lovesoongalarm.lovesoongalarm.domain.location.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.LocationRequest;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.facade.LocationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Tag(name = "Location", description = "위치정보 API")
public class LocationController {
    private final LocationFacade locationFacade;

    @Operation(summary = "위치 업데이트"
            , description = "위치 정보를 업데이트하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "위치 업데이트 성공")
    @PostMapping("/update")
    public BaseResponse<Void> update(
            @UserId Long userId,
            @RequestBody LocationRequest locationRequest
    ) {
        locationFacade.updateLocation(userId, locationRequest.latitude(), locationRequest.longitude());
        return BaseResponse.success(null);
    }

    @Operation(summary = "주변사람 검색"
            , description = "주변 50m 안에 있는 사람을 검색하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "주변사람 검색 성공")
    @GetMapping("/nearby")
    public BaseResponse<NearbyResponseDTO> nearby(
            @UserId Long userId
    ) {
        return BaseResponse.success(locationFacade.findNearby(userId));
    }
}
