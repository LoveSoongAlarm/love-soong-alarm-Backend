package com.lovesoongalarm.lovesoongalarm.domain.notification.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter {
    public NotificationResponseDTO toNoticeResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .matchingUserId(notification.getMatchingUserId())
                .notificationTime(notification.getNotificationTime())
                .message(notification.getMessage())
                .status(notification.getStatus().getValue())
                .build();
    }
}
