package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import lombok.Builder;

@Builder
public record NotificationBadgeUpdateEvent(
        Long userId,
        boolean hasUnRead
) {
}
