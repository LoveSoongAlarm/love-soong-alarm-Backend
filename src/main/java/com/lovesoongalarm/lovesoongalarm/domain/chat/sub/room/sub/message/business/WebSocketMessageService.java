package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageService {

    private final MessageSender messageSender;

    public void sendConnectionSuccessMessage(Long userId, String userNickname, WebSocketSession session) {
        WebSocketMessageDTO.ConnectionInfo connectionInfo = WebSocketMessageDTO.ConnectionInfo.builder()
                .type(EWebSocketMessageType.CONNECTION_SUCCESS)
                .userId(userId)
                .userNickname(userNickname)
                .timestamp(LocalDateTime.now())
                .message("WebSocket 연결이 성공했습니다.")
                .build();

        messageSender.sendMessage(session, connectionInfo);
    }

    public void sendErrorMessage(WebSocketSession session, String errorCode, String message) {
        if (!session.isOpen()) {
            log.warn("세션이 닫혀있어 에러 메시지를 전송할 수 없습니다.");
            return;
        }

        WebSocketMessageDTO.ErrorResponse errorResponse = WebSocketMessageDTO.ErrorResponse.builder()
                .type(EWebSocketMessageType.ERROR)
                .errorCode(errorCode)
                .message(message)
                .build();

        messageSender.sendMessage(session, errorResponse);
    }

    public void sendSubscribeSuccessMessage(WebSocketSession session, Long chatRoomId) {
        WebSocketMessageDTO.SubscribeSuccess subscribeSuccess = WebSocketMessageDTO.SubscribeSuccess.builder()
                .type(EWebSocketMessageType.SUBSCRIBE)
                .chatRoomId(chatRoomId)
                .message("채팅방 구독에 성공했습니다.")
                .build();

        messageSender.sendMessage(session, subscribeSuccess);
    }

    public void sendUnsubscribeSuccessMessage(WebSocketSession session, Long chatRoomId) {
        WebSocketMessageDTO.SubscribeSuccess unsubscribeSuccess = WebSocketMessageDTO.SubscribeSuccess.builder()
                .type(EWebSocketMessageType.UNSUBSCRIBE)
                .chatRoomId(chatRoomId)
                .message("채팅방 구독 해제에 성공했습니다.")
                .build();

        messageSender.sendMessage(session, unsubscribeSuccess);
    }

    public void sendReadMessage(WebSocketSession session, Long chatRoomId) {
        WebSocketMessageDTO.MessageReadNotification messageReadNotification = WebSocketMessageDTO.MessageReadNotification.builder()
                .type(EWebSocketMessageType.MESSAGE_READ)
                .chatRoomId(chatRoomId)
                .build();

        messageSender.sendMessage(session, messageReadNotification);
    }

    public void sendMessage(WebSocketSession session, Message message, boolean isSentByMe){
        WebSocketMessageDTO.ChatMessage chatMessage = WebSocketMessageDTO.ChatMessage.builder()
                .type(EWebSocketMessageType.CHAT_MESSAGE)
                .messageId(message.getId())
                .content(message.getContent())
                .timestamp(message.getCreatedAt())
                .isSentByMe(isSentByMe)
                .build();

        messageSender.sendMessage(session, chatMessage);
    }
}
