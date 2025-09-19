package com.lovesoongalarm.lovesoongalarm.domain.websocket.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageRouter {

    private final ObjectMapper objectMapper;
    private final MessageSender messageSender;
    private final ChatService chatService;

    public void routeMessage(WebSocketSession session, String payload) {
        try {
            WebSocketMessageDTO.Request request = parseMessage(payload);
            Long userId = extractUserId(session);

            validateUser(userId, session);

            switch (request.type()) {
                case SUBSCRIBE -> handleSubscribe(session, request, userId);
                case UNSUBSCRIBE -> handleUnsubscribe(session, request, userId);
                case MESSAGE_SEND -> handleSendMessage(session, request, userId);
                default -> handleUnknownMessageType(session, request.type());
            }

        } catch (Exception e) {
            log.error("메시지 처리 중 에러 발생", e);
            messageSender.sendErrorMessage(session, "PROCESSING_ERROR", "메시지 처리 중 오류가 발생했습니다:" + e.getMessage());
        }
    }

    private Long extractUserId(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

    private WebSocketMessageDTO.Request parseMessage(String payload) throws Exception {
        return objectMapper.readValue(payload, WebSocketMessageDTO.Request.class);
    }

    private void validateUser(Long userId, WebSocketSession session) {
        if (userId == null) {
            messageSender.sendErrorMessage(session, "UNAUTHORIZED", "인증되지 않은 사용자입니다.");
            throw new SecurityException("Unauthorized user");
        }
    }

    private void handleSubscribe(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleSubscribe(session, request.chatRoomId(), userId);
        } catch (CustomException e) {
            log.warn("구독 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            messageSender.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("구독 처리 중 예외 발생", e);
            messageSender.sendErrorMessage(session, "SUBSCRIPTION_ERROR", "구독 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleUnsubscribe(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleUnsubscribe(session, request.chatRoomId(), userId);
        } catch (CustomException e) {
            log.warn("구독 해제 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            messageSender.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("구독 처리 중 예외 발생", e);
            messageSender.sendErrorMessage(session, "UNSUBSCRIPTION_ERROR", "구독 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleSendMessage(WebSocketSession session, WebSocketMessageDTO.Request request, Long userId) {
        try {
            chatService.handleSendMessage(request.chatRoomId(), request.content(), userId);
        } catch (CustomException e) {
            log.warn("메시지 전송 실패 - 채팅방: {}, 유저: {}, 이유: {}", request.chatRoomId(), userId, e.getErrorCode().getMessage());
            messageSender.sendErrorMessage(session, e.getErrorCode().getStatus().toString(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("메시지 전송 처리 중 예외 발생", e);
            messageSender.sendErrorMessage(session, "MESSAGE_SEND_ERROR", "메시지 전송 중 오류가 발생했습니다.");
        }
    }

    private void handleUnknownMessageType(WebSocketSession session, EWebSocketMessageType messageType) {
        log.warn("알 수 없는 메시지 타입 - sessionId: {}, type: {}", session.getId(), messageType);
        messageSender.sendErrorMessage(session, "UNKNOWN_TYPE",
                "알 수 없는 메시지 타입입니다: " + messageType);
    }
}
