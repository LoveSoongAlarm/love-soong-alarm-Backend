package com.lovesoongalarm.lovesoongalarm.domain.notification.event.listener;

import com.lovesoongalarm.lovesoongalarm.domain.notification.business.WebSocketNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationStatusAllChangeEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationBadgeUpdateEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationCreatedEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationStatusChangeEvent;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final SessionService sessionService;
    private final WebSocketNotificationService webSocketNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationCreatedEvent event) {
        for (NotificationCreatedEvent.NotificationHolder holder : event.notifications()) {
            WebSocketSession session = sessionService.getSession(holder.userId());
            log.info("세션 조회 : userId={}, session={}", holder.userId(), session);
            if (session != null && session.isOpen()) {
                webSocketNotificationService.sendNotification(session, holder.notification());
                log.info("웹소켓 알림 전송 완료 - userId={}", holder.userId());
            } else {
                log.debug("세션 없음/닫힘 - 알림 전송 생략 - userId={}", holder.userId());
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdateBadgeEvent(NotificationBadgeUpdateEvent event) {
        WebSocketSession session = sessionService.getSession(event.userId());
        if (session != null && session.isOpen()) {
            webSocketNotificationService.sendUnreadBadgeUpdate(
                    session,
                    event.hasUnRead()
            );

            log.info("웹소켓 알림 뱃지 상태 업데이트 완료 - userId={}", event.userId());
        } else {
            log.error("세션 없음/닫힘 - 웹소켓 알림 뱃지 상태 업데이트 생략 - userId={}", event.userId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationReadEvent(NotificationStatusChangeEvent event) {
        WebSocketSession session = sessionService.getSession(event.userId());
        if (session != null && session.isOpen()) {
            webSocketNotificationService.sendReadNotification(
                    session,
                    event.notificationId()
            );

            log.info("웹소켓 알림 읽음 상태 전송 완료 - userId={}, notificationId={}",
                    event.userId(), event.notificationId());
        } else {
            log.debug("세션 없음/닫힘 - 읽음 상태 전송 생략 - userId={}", event.userId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationAllReadEvent(NotificationStatusAllChangeEvent event) {
        WebSocketSession session = sessionService.getSession(event.userId());
        if (session != null && session.isOpen()) {
            webSocketNotificationService.sendAllReadNotification(
                    session,
                    event.isAll()
            );
            log.info("웹소켓 알림 전체 읽음 상태 전송 완료 - userId={}", event.userId());
        } else {
            log.debug("세션 없음/닫힘 - 전체 읽음 상태 전송 생략 - userId={}", event.userId());
        }
    }
}
