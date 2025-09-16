package com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENoticeStatus {
    READ("읽음"),
    NOT_READ("안읽음");

    private final String value;
}
