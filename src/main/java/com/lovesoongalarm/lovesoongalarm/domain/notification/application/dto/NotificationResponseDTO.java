package com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto;

import lombok.Builder;

@Builder
public record NotificationResponseDTO(
        Long matchingUserId,
        String message,
        String status,
        String notificationTime
) {
}
