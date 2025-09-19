package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationWebSocketDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationRetriever;
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
    private final NotificationRetriever notificationRetriever;

    public void sendNotification(WebSocketSession session, Notification notification) {
        NotificationWebSocketDTO.Notification notificationWebSocketDTO = NotificationWebSocketDTO.Notification.builder()
                .type(EWebSocketNotificationType.NOTIFICATION)
                .notificationId(notification.getId())
                .matchingUserId(notification.getMatchingUserId())
                .message(notification.getMessage())
                .build();

        messageSender.sendNotification(session, notificationWebSocketDTO);
    }

    public void sendUnreadBadgeUpdate(WebSocketSession session, boolean hasUnread) {
        messageSender.sendUnreadBadgeUpdate(session, hasUnread);
    }

    public void sendUnreadBadgeUpdate(WebSocketSession session, Long userId) {
        boolean hasRead = notificationRetriever.existsByUserIdAndStatus(userId);
        messageSender.sendUnreadBadgeUpdate(session, hasRead);
    }

    public void sendReadNotification(WebSocketSession session, Long notificationId) {
        messageSender.sendReadNotification(session, notificationId);
    }

    public void sendAllReadNotification(WebSocketSession session, boolean allRead) {
        messageSender.sendAllReadNotification(session, allRead);
    }
}
