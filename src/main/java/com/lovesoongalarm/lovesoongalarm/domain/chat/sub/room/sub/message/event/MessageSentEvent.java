package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import lombok.Builder;

@Builder
public record MessageSentEvent(
        Long chatRoomId,
        Message message,
        Long senderId
) {
}
