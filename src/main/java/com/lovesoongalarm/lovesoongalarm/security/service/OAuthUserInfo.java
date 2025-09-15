package com.lovesoongalarm.lovesoongalarm.security.service;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuthUserInfo {
    private final OAuthProviderFactory oAuthProviderFactory;

    public void revoke(EPlatform platform, String serialId) {
        OAuthProvider oAuthProvider = oAuthProviderFactory.findProvider(platform);
        oAuthProvider.requestRevoke(serialId);
    }
}
