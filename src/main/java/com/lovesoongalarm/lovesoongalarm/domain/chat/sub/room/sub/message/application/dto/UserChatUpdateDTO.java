package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserChatUpdateDTO(
        Long chatRoomId,
        String lastMessageContent,
        LocalDateTime timestamp,
        boolean isMyMessage,
        boolean isRead
) {
}
