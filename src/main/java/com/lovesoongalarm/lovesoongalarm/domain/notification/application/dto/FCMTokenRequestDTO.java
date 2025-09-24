package com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EDeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FCMTokenRequestDTO(
        @Schema(description = "FCM 토큰", example = "dQw4w9WgXcQ:APA91b...")
        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String fcmToken,

        @Schema(description = "디바이스 타입", example = "WEB")
        @NotNull(message = "디바이스 타입은 필수입니다.")
        EDeviceType deviceType
) {
}
