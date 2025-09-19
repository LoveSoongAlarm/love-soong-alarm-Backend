package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import lombok.Builder;

@Builder
public record NotificationStatusAllChangeEvent(
        Long userId,
        boolean isAll
) {
}
