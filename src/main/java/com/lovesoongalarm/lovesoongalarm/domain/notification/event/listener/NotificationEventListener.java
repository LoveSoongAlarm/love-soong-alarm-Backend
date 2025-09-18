package com.lovesoongalarm.lovesoongalarm.domain.notification.event.listener;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.WebSocketNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final ChatSessionService sessionManager;
    private final WebSocketNotificationService webSocketNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationCreatedEvent event) {
        for (NotificationCreatedEvent.NotificationHolder holder : event.notifications()) {
            WebSocketSession session = sessionManager.getSession(holder.userId());
            log.info("세션 조회 : userId={}, session={}", holder.userId(), session);
            if (session != null && session.isOpen()) {
                webSocketNotificationService.sendNotification(session, holder.notification());
                log.info("웹소켓 알림 전송 완료 - userId={}", holder.userId());
            } else {
                log.debug("세션 없음/닫힘 - 알림 전송 생략 - userId={}", holder.userId());
            }
        }
    }
}
