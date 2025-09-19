package com.lovesoongalarm.lovesoongalarm.domain.notification.event;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
public record NotificationCreatedEvent(
        List<NotificationHolder> notifications
) {
    public record NotificationHolder(
            Long userId,
            Notification notification
    ) {}
}
