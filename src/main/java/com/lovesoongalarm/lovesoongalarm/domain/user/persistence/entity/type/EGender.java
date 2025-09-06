package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EGender {
    MALE("남성"),
    FEMALE("여성");

    private final String value;
}
