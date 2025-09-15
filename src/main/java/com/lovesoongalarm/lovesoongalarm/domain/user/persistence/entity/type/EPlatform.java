package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPlatform {

    KAKAO("KAKAO"),
    APPLE("APPLE");

    private static final String SUFFIX = "OAuthProvider";
    private final String loginPlatform;

    public String getBeanName() {
        return loginPlatform.toLowerCase() + SUFFIX;
    }
}