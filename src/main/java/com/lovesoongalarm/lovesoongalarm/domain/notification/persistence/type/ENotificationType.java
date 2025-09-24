package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENotificationType {
    READ("메시지 읽음", "💬"),
    DELETE("메시지 삭제", "💬"),
    CHAT_MESSAGE("채팅 메시지", "💬");

    private final String defaultEmoji;
    private final String description;

    public String getTitle(String senderName) {
        return switch (this) {
            case READ -> null;
            case DELETE -> null;
            case CHAT_MESSAGE -> String.format("%s님의 메시지", senderName);
        };
    }
}
