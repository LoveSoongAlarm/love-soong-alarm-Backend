package com.lovesoongalarm.lovesoongalarm.external.kakao;

import com.lovesoongalarm.lovesoongalarm.security.service.OAuthProvider;
import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.external.kakao.openfeign.KakaoFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProvider implements OAuthProvider {

    private final KakaoFeignClient kakaoFeignClient;

    @Value("${oauth.kakao.key}")
    private String adminKey;

    @Override
    public void requestRevoke(String serialId) {
        kakaoFeignClient.unlinkKakaoServer(Constants.AUTHORIZATION_PREFIX + adminKey, Constants.TARGET_ID_TYPE, Long.parseLong(serialId));
    }
}
