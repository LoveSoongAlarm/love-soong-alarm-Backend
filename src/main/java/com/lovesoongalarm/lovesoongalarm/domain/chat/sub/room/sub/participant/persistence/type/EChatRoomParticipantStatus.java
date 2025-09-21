package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EChatRoomParticipantStatus {
    PENDING("채팅 대기"),
    JOINED("채팅 입장"),
    BANNED("채팅방 밴");

    private final String value;

    public boolean canSendMessage() {
        return this == JOINED;
    }

    public boolean isBanned() {
        return this == BANNED;
    }
}
