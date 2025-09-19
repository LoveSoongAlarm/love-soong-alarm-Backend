package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import lombok.Builder;

@Builder
public record NotificationAllReadEvent(
        Long userId,
        boolean allRead
) {
}
