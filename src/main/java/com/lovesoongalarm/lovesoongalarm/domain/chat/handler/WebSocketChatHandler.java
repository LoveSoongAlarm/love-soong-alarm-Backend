package com.lovesoongalarm.lovesoongalarm.domain.chat.handler;

import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatService;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final UserQueryService userQueryService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹소켓 연결 성공");

        try {
            Long userId = (Long) session.getAttributes().get("userId");
            String userNickname = userQueryService.getUserNickname(userId);
            chatService.registerSession(userId, userNickname, session);
        } catch (Exception e) {
            log.error("WebSocket 연결 처리 중 오류 발생", e);
            session.close();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("메시지 수신");
        log.info("메시지: {}", message.getPayload());


    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("웹소켓 연결 종료");

        Long userId = (Long) session.getAttributes().get("userId");
        log.info("세션 ID: {}, 사용자 ID: {}, 종료 상태: {}", session.getId(), userId, status);

        if (userId != null) {
            chatService.removeSession(userId);
        }
    }
}
