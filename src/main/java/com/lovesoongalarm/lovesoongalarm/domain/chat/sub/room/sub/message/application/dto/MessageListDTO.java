package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class MessageListDTO {

    @Schema(name = "MessageListResponse", description = "과거 메시지 조회 응답")
    @Builder
    public record Response(
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
