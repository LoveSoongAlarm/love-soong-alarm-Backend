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
    public record UnreadNotificationBadge(
            EWebSocketNotificationType type,
            boolean hasUnread
    ) {
    }

    @Builder
    public record ReadNotification(
            EWebSocketNotificationType type,
            Long notificationId
    ) {
    }

    @Builder
    public record AllReadNotification(
            EWebSocketNotificationType type,
            boolean allRead
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
