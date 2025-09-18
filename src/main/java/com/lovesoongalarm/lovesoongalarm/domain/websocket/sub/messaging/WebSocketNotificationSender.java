package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationSender {

    private final SessionService sessionService;
    private final MessageSender messageSender;

    public void sendMessageToUser(Long userId, Message message, boolean isSentByMe) {
        log.info("사용자에게 실시간 메시지 전송 시작 - userId: {}, messageId: {}, isSentByMe: {}",
                userId, message.getId(), isSentByMe);

        try {
            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.info("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendMessage(session, message, isSentByMe,
                    message.getChatRoom().getId(), message.getUser().getId());
            log.info("실시간 메시지 전송 완료 - userId: {}, messageId: {}", userId, message.getId());
        } catch (Exception e) {
            log.error("실시간 메시지 전송 실패 - userId: {}, messageId: {}",
                    userId, message.getId(), e);
        }
    }

    public void sendReadNotification(Long userId, Long chatRoomId, Long readerId) {
        log.info("읽음 상태 알림 전송 - userId: {}, chatRoomId: {}, readerId: {}",
                userId, chatRoomId, readerId);

        try {
            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("읽음 알림 전송 불가 - 세션 없음, userId: {}", userId);
                return;
            }

            messageSender.sendReadMessage(session, chatRoomId, readerId);
            log.info("읽음 상태 알림 전송 완료 - userId: {}", userId);
        } catch (Exception e) {
            log.error("읽음 상태 알림 전송 실패 - userId: {}", userId, e);
        }
    }

    public void sendUnreadBadgeUpdate(Long userId, int totalUnreadCount) {
        log.info("안 읽은 메시지 배지 업데이트 전송 - userId: {}, count: {}", userId, totalUnreadCount);

        try {
            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("배지 업데이트 전송 불가 - 세션 없음, userId: {}", userId);
                return;
            }

            messageSender.sendUnreadBadgeUpdate(session, totalUnreadCount);
            log.info("배지 업데이트 전송 완료 - userId: {}, count: {}", userId, totalUnreadCount);
        } catch (Exception e) {
            log.error("배지 업데이트 전송 실패 - userId: {}", userId, e);
        }
    }
}
