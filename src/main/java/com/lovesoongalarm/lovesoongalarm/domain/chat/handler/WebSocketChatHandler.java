package com.lovesoongalarm.lovesoongalarm.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.WebSocketMessageService;
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
    private final WebSocketMessageService webSocketMessageService;

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹소켓 연결 성공");

        try {
            Long userId = (Long) session.getAttributes().get("userId");
            String userNickname = userQueryService.getUserNickname(userId);
            chatService.registerSession(userId, userNickname, session);
            chatService.subscribeToUserChatUpdates(userId, session);
        } catch (Exception e) {
            log.error("WebSocket 연결 처리 중 오류 발생", e);
            session.close();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("메시지 수신");
        log.info("메시지: {}", message.getPayload());

        try {
            WebSocketMessageDTO.Request request = objectMapper.readValue(
                    message.getPayload(), WebSocketMessageDTO.Request.class
            );

            Long userId = (Long) session.getAttributes().get("userId");
            if (userId == null) {
                webSocketMessageService.sendErrorMessage(session, "UNAUTHORIZED", "인증되지 않은 사용자입니다.");
                return;
            }

            log.info("메시지 타입: {}, 채팅방 ID: {}, 사용자 ID: {}", userId, session.getId(), userId);

            switch (request.type()) {
                case SUBSCRIBE:
                    handleSubscribe(session, request, userId);
                    break;
                case UNSUBSCRIBE:
                    handleUnsubscribe(session, request, userId);
                    break;
                case MESSAGE_SEND:
                    handleSendMessage(session, request, userId);
                    break;
                default:
                    webSocketMessageService.sendErrorMessage(session, "UNKNOWN_TYPE", "알 수 없는 메시지 타입입니다:" + request.type());
            }

        } catch (Exception e) {
            log.error("메시지 처리 중 에러 발생", e);
            webSocketMessageService.sendErrorMessage(session, "PROCESSING_ERROR", "메시지 처리 중 오류가 발생했습니다:" + e.getMessage());
        }
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
            chatService.subscribeToUserChatUpdates(userId, session);
        }
    }

    private void handleSubscribe(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleSubscribe(session, request.chatRoomId(), userId);
        } catch (CustomException e) {
            log.warn("구독 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            webSocketMessageService.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("구독 처리 중 예외 발생", e);
            webSocketMessageService.sendErrorMessage(session, "SUBSCRIPTION_ERROR", "구독 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleUnsubscribe(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleUnsubscribe(session, request.chatRoomId(), userId);
        } catch (CustomException e) {
            log.warn("구독 해제 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            webSocketMessageService.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("구독 처리 중 예외 발생", e);
            webSocketMessageService.sendErrorMessage(session, "UNSUBSCRIPTION_ERROR", "구독 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleSendMessage(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleSendMessage(request.chatRoomId(), request.content(), userId);
        } catch (CustomException e) {
            log.warn("메시지 전송 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            webSocketMessageService.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("메시지 전송 처리 중 예외 발생", e);
            webSocketMessageService.sendErrorMessage(session, "MESSAGE_SEND_ERROR", "메시지 전송 중 오류가 발생했습니다.");
        }
    }
}
