package com.lovesoongalarm.lovesoongalarm.domain.notification.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.FCMTokenRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.PushNotificationRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.FCMPushService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.FCMTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "FCM", description = "푸시 알림 API")
public class FCMController {

    private final FCMTokenService fcmTokenService;

    @PostMapping("/token")
    @Operation(summary = "FCM 토큰 등록",
            description = "사용자의 FCM 토큰을 등록하거나 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공")
    public BaseResponse<Void> registerToken(
            @UserId Long userId,
            @Valid @RequestBody FCMTokenRequestDTO request
    ) {
        log.info("FCM 토큰 등록 요청 - userId: {}, deviceType: {}", userId, request.deviceType());
        fcmTokenService.registerToken(userId, request);
        return BaseResponse.success(null);
    }

    @DeleteMapping("/token")
    @Operation(summary = "FCM 토큰 삭제",
            description = "로그아웃 시 사용자의 FCM 토큰을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "FCM 토큰 삭제 성공")
    public BaseResponse<Void> deleteToken(
            @UserId Long userId,
            @RequestParam @NotBlank(message = "토큰은 필수입니다.") String token
    ) {
        log.info("FCM 토큰 삭제 요청 - userId: {}", userId);
        fcmTokenService.deleteToken(token);
        return BaseResponse.success(null);
    }
}
