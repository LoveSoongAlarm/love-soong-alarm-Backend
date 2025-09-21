package com.lovesoongalarm.lovesoongalarm.domain.websocket.handler;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.business.WebSocketConnectionService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.business.WebSocketMessageRouter;
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

    private final WebSocketConnectionService webSocketConnectionService;
    private final WebSocketMessageRouter webSocketMessageRouter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹소켓 연결 성공");

        try {
            webSocketConnectionService.handleConnection(session);
        } catch (Exception e) {
            log.error("WebSocket 연결 처리 중 오류 발생", e);
            session.close();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("메시지 수신");
        log.info("메시지: {}", message.getPayload());
        webSocketMessageRouter.routeMessage(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("웹소켓 연결 종료");
        webSocketConnectionService.handleDisconnection(session);
    }


}
