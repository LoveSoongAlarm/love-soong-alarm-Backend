package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomBlockNotificationService {

    private final SessionService sessionService;
    private final MessageSender messageSender;

    public void notifyBlockerSuccess(Long userId, Long chatRoomId, Long targetId) {
        log.info("사용자에게 차단 성공 메시지 전송 시작 - userId: {}, chatRoomId: {}, targetId: {}",
                userId, chatRoomId, targetId);

        try {
            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.info("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendBlockSuccess(session, chatRoomId, targetId);
            log.info("사용자에게 차단 성공 메시지 전송 시작 - userId: {}, chatRoomId: {}, targetId: {}",
                    userId, chatRoomId, targetId);
        } catch (Exception e) {
            log.error("차단 성공 메시지 전송 실패 - userId: {}, chatRoomId: {}, targetId: {}, error: {}",
                    userId, chatRoomId, targetId, e);
        }
    }

    public void notifyUnblockerSuccess(Long userId, Long chatRoomId, Long targetId) {
        log.info("사용자에게 차단 해제 성공 메시지 전송 시작 - userId: {}, chatRoomId: {}, targetId: {}",
                userId, chatRoomId, targetId);

        try {
            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.info("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendUnblockSuccess(session, chatRoomId, targetId);
            log.info("사용자에게 차단 해제 성공 메시지 전송 시작 - userId: {}, chatRoomId: {}, targetId: {}",
                    userId, chatRoomId, targetId);
        } catch (Exception e) {
            log.error("차단 해제 성공 메시지 전송 실패 - userId: {}, chatRoomId: {}, targetId: {}, error: {}",
                    userId, chatRoomId, targetId, e);
        }
    }
}
