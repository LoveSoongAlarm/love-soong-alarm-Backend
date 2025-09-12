package com.lovesoongalarm.lovesoongalarm.security.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    /**
     * - Filter에서 전달한 Authentication 객체(아직 인증되지 않은 상태)를 받아서
     * - 실제 인증 로직을 처리할 수 있는 Provider에게 위임한다.
     * 여기서는 JWT 전용 Provider인 JwtAuthenticationProvider 하나만 등록되어 있어서,
     * 모든 요청을 무조건 해당 Provider에 위임한다.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("AuthenticationManager 진입");
        return jwtAuthenticationProvider.authenticate(authentication);
    }
}
