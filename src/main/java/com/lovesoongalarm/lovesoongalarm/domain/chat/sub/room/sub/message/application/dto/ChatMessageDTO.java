package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageDTO {

    @Schema(name = "ChatMessageListRequest", description = "과거 메시지 조회 요청")
    public record Request(
            Integer size,
            @NotNull
            Long lastMessageId
    ){
        public Request {
            if (size == null || size <= 0) {
                size = 20;
            } else if (size > 100) {
                size = 100;
            }
        }
    }

    @Schema(name = "ChatMessageListResponse", description = "과거 메시지 조회 응답")
    @Builder
    public record ListResponse(
            List<MessageInfo> messages,
            boolean hasMoreMessages,
            Long oldestMessageId
    ) {
    }

    @Builder
    public record MessageInfo(
            Long messageId,
            String content,
            LocalDateTime createdAt,
            boolean isSentByMe,
            boolean isRead
    ) {
    }
}
