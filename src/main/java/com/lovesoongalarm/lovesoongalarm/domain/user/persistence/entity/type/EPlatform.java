package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EPlatform {
    KAKAO("kakao");

    private final String value;
}
