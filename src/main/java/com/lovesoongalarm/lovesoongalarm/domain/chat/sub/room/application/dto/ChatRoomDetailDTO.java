package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDetailDTO {

    @Schema(name = "ChatRoomDetailResponse", description = "초기 채팅방 조회 응답")
    @Builder
    public record Response(
            PartnerInfo partner,
            List<MessageInfo> recentMessages,
            boolean hasMoreMessages,
            Long oldestMessageId
    ) {
    }

    @Builder
    public record PartnerInfo(
            Long userId,
            String nickname,
            String emoji,
            Integer age,
            String major,
            List<InterestInfo> interests
    ) {
    }

    @Builder
    public record InterestInfo(
            EDetailLabel label,
            List<String> hashtags
    ) {
    }

    @Builder
    public record MessageInfo(
            Long messageId,
            String content,
            LocalDateTime createdAt,
            boolean isSentByMe
    ) {
    }
}
