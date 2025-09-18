package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EWebSocketNotificationType {
    NOTIFICATION("매칭알림"),
    ERROR("에러")
    ;

    private final String value;
}
