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
    private final FCMPushService fcmPushService;

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

    @PostMapping("/test/send")
    @Operation(summary = "테스트 푸시 알림 전송",
            description = "개발/테스트용 푸시 알림을 전송합니다. (운영환경에서는 사용하지 마세요)")
    @ApiResponse(responseCode = "200", description = "테스트 푸시 알림 전송 성공")
    public BaseResponse<Void> sendTestPush(
            @UserId Long userId,
            @Valid @RequestBody PushNotificationRequestDTO request
    ) {
        log.info("테스트 푸시 알림 전송 요청 - userId: {}, targetUserId: {}, type: {}",
                userId, request.targetUserId(), request.type());

        fcmPushService.sendToUser(
                request.targetUserId(),
                request.title(),
                request.body(),
                request.data()
        );

        return BaseResponse.success(null);
    }

    @PostMapping("/test/send-to-me")
    @Operation(summary = "나에게 테스트 푸시 전송",
            description = "자신에게 테스트 푸시 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "테스트 푸시 알림 전송 성공")
    public BaseResponse<Void> sendTestPushToMe(
            @UserId Long userId,
            @RequestBody Map<String, String> request
    ) {
        String title = request.getOrDefault("title", "테스트 알림");
        String body = request.getOrDefault("body", "푸시 알림 테스트 메시지입니다.");

        log.info("나에게 테스트 푸시 전송 - userId: {}, title: {}", userId, title);

        Map<String, String> data = Map.of(
                "type", "TEST",
                "timestamp", String.valueOf(System.currentTimeMillis())
        );

        fcmPushService.sendToUser(userId, title, body, data);
        return BaseResponse.success(null);
    }
}
