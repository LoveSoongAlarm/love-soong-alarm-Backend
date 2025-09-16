package com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EWebSocketMessageType {
    CONNECTION_SUCCESS("연결 완료"),
    SUBSCRIBE("채팅방 구독"),
    UNSUBSCRIBE("채팅방 구독해제"),
    MESSAGE_READ("메시지 읽음"),
    ERROR("에러");

    private final String value;
}
