package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import lombok.Builder;

@Builder
public record NotificationStatusChangeEvent(
        Long userId,
        Long notificationId
) {
}
