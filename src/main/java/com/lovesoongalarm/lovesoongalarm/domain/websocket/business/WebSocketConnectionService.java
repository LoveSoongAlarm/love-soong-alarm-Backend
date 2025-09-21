package com.lovesoongalarm.lovesoongalarm.domain.websocket.business;

import com.lovesoongalarm.lovesoongalarm.domain.notification.business.WebSocketNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserQueryService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketConnectionService {

    private final SessionService sessionService;
    private final UserQueryService userQueryService;
    private final MessageSender messageSender;
    private final SubscriptionService subscriptionService;
    private final WebSocketNotificationService webSocketNotificationService;

    public void handleConnection(WebSocketSession session) {
        Long userId = extractUserId(session);
        String userNickname = userQueryService.getUserNickname(userId);
        sessionService.addSession(userId, session);
        subscriptionService.subscribeToChatBadgeUpdate(session, userId);
        messageSender.sendConnectionSuccessMessage(userId, userNickname, session);

        webSocketNotificationService.sendUnreadBadgeUpdate(session, userId);
    }

    public void handleDisconnection(WebSocketSession session) {
        Long userId = extractUserId(session);
        if (userId != null) {
            sessionService.removeSession(userId);
        }
    }

    private Long extractUserId(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}