package com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record PushNotificationRequestDTO(
        @Schema(description = "대상 사용자 ID", example = "1")
        @NotNull(message = "대상 사용자 ID는 필수입니다.")
        Long targetUserId,

        @Schema(description = "알림 제목", example = "새 메시지가 도착했습니다")
        @NotBlank(message = "알림 제목은 필수입니다.")
        String title,

        @Schema(description = "알림 내용", example = "안녕하세요!")
        @NotBlank(message = "알림 내용은 필수입니다.")
        String body,

        @Schema(description = "추가 데이터", example = "{\"chatRoomId\": \"123\", \"type\": \"CHAT_MESSAGE\"}")
        Map<String, String> data,

        @Schema(description = "알림 타입", example = "CHAT_MESSAGE")
        ENotificationType type
) {
}
