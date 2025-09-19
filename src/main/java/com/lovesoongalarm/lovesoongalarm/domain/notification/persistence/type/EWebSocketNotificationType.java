package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EWebSocketNotificationType {
    NOTIFICATION("매칭알림"),
    UNREAD_NOTIFICATION_BADGE_UPDATE("안 읽은 알림 배지 업데이트"),
    READ_NOTIFICATION("알림 읽음"),
    READ_ALL_NOTIFICATION("전체 알림 읽음"),
    DELETE_NOTIFICATION("알림 삭제"),
    DELETE_ALL_NOTIFICATION("전체 알림 삭제"),
    NOTIFICATION_LIST_UPDATED("알림 목록 갱신"),
    ERROR("에러")
    ;

    private final String value;
}
