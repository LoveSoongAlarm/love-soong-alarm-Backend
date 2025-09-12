package com.lovesoongalarm.lovesoongalarm.security.info.factory;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import com.lovesoongalarm.lovesoongalarm.security.info.KakaoOauth2UserInfo;

import java.util.Map;

public class Oauth2UserInfoFactory {

    public static Oauth2UserInfo getOauth2UserInfo(
            EPlatform platform,
            Map<String, Object> attributes
    ) {
        Oauth2UserInfo ret;
        switch (platform) {
            case KAKAO -> ret = new KakaoOauth2UserInfo(attributes);
            default -> throw new IllegalAccessError("잘못된 플랫폼입니다.");
        }

        return ret;
    }
}
