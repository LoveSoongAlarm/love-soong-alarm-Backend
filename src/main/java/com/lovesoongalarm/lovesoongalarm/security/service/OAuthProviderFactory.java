package com.lovesoongalarm.lovesoongalarm.security.service;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthProviderFactory {

    @PostConstruct
    void logProviders() {
        System.out.println("[OAuthProviderFactory] keys=" + oAuthProviders.keySet());
    }

    private final Map<String, OAuthProvider> oAuthProviders;

    public OAuthProvider findProvider(final EPlatform platform) {
        return oAuthProviders.get(platform.getBeanName());
    }
}
