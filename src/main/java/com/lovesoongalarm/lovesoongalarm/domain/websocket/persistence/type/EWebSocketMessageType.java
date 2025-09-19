package com.lovesoongalarm.lovesoongalarm.domain.websocket.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EWebSocketMessageType {
    CONNECTION_SUCCESS("연결 완료"),
    SUBSCRIBE("채팅방 구독"),
    UNSUBSCRIBE("채팅방 구독해제"),
    MESSAGE_SEND("메시지 송신"),
    MESSAGE_READ("메시지 읽음"),
    CHAT_MESSAGE("채팅 메시지"),
    UNREAD_BADGE_UPDATE("안 읽은 메시지 배지 업데이트"),
    CHAT_LIST_UPDATE("채팅방 목록 업데이트"),
    MESSAGE_COUNT_LIMIT("채팅방 메시지 제한"),
    ERROR("에러");

    private final String value;
}
