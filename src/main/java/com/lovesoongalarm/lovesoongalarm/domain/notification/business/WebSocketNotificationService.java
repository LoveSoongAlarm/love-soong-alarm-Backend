package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationWebSocketDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EWebSocketNotificationType;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
    private final MessageSender messageSender;

    public void sendNotification(WebSocketSession session, Notification notification) {
        NotificationWebSocketDTO.Notification notificationWebSocketDTO = NotificationWebSocketDTO.Notification.builder()
                .type(EWebSocketNotificationType.NOTIFICATION)
                .notificationId(notification.getId())
                .matchingUserId(notification.getMatchingUserId())
                .message(notification.getMessage())
                .build();

        messageSender.sendNotification(session, notificationWebSocketDTO);
    }
}
