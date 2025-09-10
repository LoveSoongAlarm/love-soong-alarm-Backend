package com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EWebSocketMessageType {
    CONNECTION_SUCCESS("연결 완료");

    private final String value;
}
