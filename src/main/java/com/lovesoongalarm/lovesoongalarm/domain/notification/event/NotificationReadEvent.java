package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import lombok.Builder;

@Builder
public record NotificationReadEvent(
        Long userId,
        Long notificationId
) {
}
