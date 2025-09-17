package com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import lombok.Builder;

import java.time.LocalDateTime;

public class WebSocketMessageDTO {

    public record Request(
            EWebSocketMessageType type,
            Long chatRoomId,
            String content
    ) {
    }

    @Builder
    public record ConnectionInfo(
            EWebSocketMessageType type,
            Long userId,
            String userNickname,
            LocalDateTime timestamp,
            String message
    ) {
    }

    @Builder
    public record SubscribeSuccess(
            EWebSocketMessageType type,
            Long chatRoomId,
            String message
    ) {
    }

    @Builder
    public record MessageReadNotification(
            EWebSocketMessageType type,
            Long chatRoomId,
            Long readerId
    ) {
    }

    @Builder
    public record ChatMessage(
            EWebSocketMessageType type,
            Long chatRoomId,
            Long senderId,
            Long messageId,
            String content,
            LocalDateTime timestamp,
            boolean isSentByMe
    ) {
    }

    @Builder
    public record ErrorResponse(
            EWebSocketMessageType type,
            String errorCode,
            String message
    ) {
    }
}
