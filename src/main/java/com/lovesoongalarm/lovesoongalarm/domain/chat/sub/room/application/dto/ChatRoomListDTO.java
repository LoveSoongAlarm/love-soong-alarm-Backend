package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomListDTO {

    @Schema(name = "ChatRoomListResponse", description = "채팅방 목록 조회 응답")
    public record Response(
            List<ChatRoomInfo> chatRooms
    ) {
    }

    @Builder
    public record ChatRoomInfo(
            Long chatRoomId,
            String partnerNickname,
            LastMessageInfo lastMessageInfo
    ) {
    }

    @Builder
    public record LastMessageInfo(
            String content,
            LocalDateTime timestamp,
            boolean isSentByMe,
            boolean isRead
    ) {
    }
}
