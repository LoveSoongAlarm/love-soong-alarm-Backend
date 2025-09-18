package com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EWebSocketNotificationType;
import lombok.Builder;

public class NotificationWebSocketDTO {
    @Builder
    public record Notification(
            EWebSocketNotificationType type,
            Long notificationId,
            Long matchingUserId,
            String message
    ) {
    }

    @Builder
    public record Error(
            EWebSocketNotificationType type,
            String errorCode,
            String message
    ) {
    }
}
