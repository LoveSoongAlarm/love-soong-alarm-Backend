package com.lovesoongalarm.lovesoongalarm.domain.websocket.dto;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.persistence.type.EWebSocketMessageType;
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
    public record ChatListSubscribeSuccess(
            EWebSocketMessageType type,
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
    public record UnreadBadgeUpdate(
            EWebSocketMessageType type,
            int totalUnreadCount
    ) {
    }

    @Builder
    public record ChatListUpdate(
            EWebSocketMessageType type,
            Long chatRoomId,
            String lastMessageContent,
            LocalDateTime timestamp,
            boolean isMyMessage,
            boolean isRead
    ) {
    }

    @Builder
    public record NewChatRoomNotification(
            EWebSocketMessageType type,
            Long chatRoomId,
            String partnerNickname,
            String partnerEmoji,
            LocalDateTime createdAt
    ) {
    }

    @Builder
    public record MessageCountLimit(
            EWebSocketMessageType type
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
