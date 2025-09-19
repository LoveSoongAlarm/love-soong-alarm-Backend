package com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto;

import lombok.Builder;

@Builder
public record NotificationResponseDTO(
        Long id,
        Long matchingUserId,
        String message,
        boolean isRead,
        String notificationTime
) {
}
