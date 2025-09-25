package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENotificationType {
    READ("메시지 읽음", "💬"),
    DELETE("메시지 삭제", "💬"),
    CHAT_MESSAGE("채팅 메시지", "💬"),
    MATCHING("매칭 알림", "💝");

    private final String defaultEmoji;
    private final String description;
}
