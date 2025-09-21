package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationType;
import lombok.Builder;

@Builder
public record NotificationStatusChangeEvent(
        ENotificationType type,
        Long userId,
        Long notificationId
) {
}
