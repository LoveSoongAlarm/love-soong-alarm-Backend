package com.lovesoongalarm.lovesoongalarm.domain.notification.application.converter;

import com.lovesoongalarm.lovesoongalarm.common.util.TimeFormatter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter {
    public NotificationResponseDTO toNoticeResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .matchingUserId(notification.getMatchingUserId())
                .notificationTime(TimeFormatter.formatTimeAgo(notification.getNotificationTime()))
                .message(notification.getMessage())
                .isRead(notification.getStatus() == ENotificationStatus.READ)
                .build();
    }
}
