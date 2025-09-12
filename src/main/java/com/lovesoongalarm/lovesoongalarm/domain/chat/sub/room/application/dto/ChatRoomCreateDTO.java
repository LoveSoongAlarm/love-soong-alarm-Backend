package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ChatRoomCreateDTO {

    @Schema(name = "ChatRoomCreateRequest", description = "채팅방 생성 요청")
    public record Request(
            @NotNull
            Long targetUserId
    ) {
    }

    @Schema(name = "ChatRoomCreateResponse", description = "채팅방 생성 응답")
    @Builder
    public record Response(
            Long chatRoomId
    ) {
    }
}
