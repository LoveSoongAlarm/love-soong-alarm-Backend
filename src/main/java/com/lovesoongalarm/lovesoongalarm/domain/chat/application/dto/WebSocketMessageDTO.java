package com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import lombok.Builder;

import java.time.LocalDateTime;

public class WebSocketMessageDTO {

    @Builder
    public record ConnectionInfo(
            EWebSocketMessageType type,
            Long userId,
            String userNickname,
            LocalDateTime timestamp,
            String message
    ) {
    }
}
