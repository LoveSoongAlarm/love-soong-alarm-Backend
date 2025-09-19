package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationType;
import lombok.Builder;

@Builder
public record NotificationStatusAllChangeEvent(
        ENotificationType type,
        Long userId,
        boolean isAll
) {
}
