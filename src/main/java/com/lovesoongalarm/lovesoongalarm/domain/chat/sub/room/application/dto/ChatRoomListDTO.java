package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomListDTO {

    @Schema(name = "ChatRoomListResponse", description = "채팅방 목록 조회 응답")
    public record Response(
            UserSlotInfo userSlotInfo,
            List<ChatRoomInfo> chatRooms
    ) {
    }

    @Builder
    public record UserSlotInfo(
            Integer maxSlot,
            Integer remainingSlot,
            boolean isPrepass
    ) {
    }

    @Builder
    public record ChatRoomInfo(
            Long chatRoomId,
            String emoji,
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
        public static LastMessageInfo empty() {
            return LastMessageInfo.builder()
                    .content("")
                    .timestamp(null)
                    .build();
        }

        public static LastMessageInfo sentByMe(String content, LocalDateTime timestamp, boolean isReadByPartner) {
            return LastMessageInfo.builder()
                    .content(content)
                    .timestamp(timestamp)
                    .isSentByMe(true)
                    .isRead(isReadByPartner)
                    .build();
        }

        public static LastMessageInfo sentByPartner(String content, LocalDateTime timestamp, boolean isReadByMe) {
            return LastMessageInfo.builder()
                    .content(content)
                    .timestamp(timestamp)
                    .isSentByMe(false)
                    .isRead(isReadByMe)
                    .build();
        }
    }
}
