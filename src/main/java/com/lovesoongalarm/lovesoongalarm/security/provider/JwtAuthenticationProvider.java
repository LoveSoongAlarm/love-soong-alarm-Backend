package com.lovesoongalarm.lovesoongalarm.security.provider;

import com.lovesoongalarm.lovesoongalarm.security.info.JwtUserInfo;
import com.lovesoongalarm.lovesoongalarm.security.info.UserPrincipal;
import com.lovesoongalarm.lovesoongalarm.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailService customUserDetailService;

    /**
     * - AuthenticationManager로부터 위임받아 실제 인증을 수행한다.
     * - supports() 메서드에서 자신이 처리할 수 있는 Authentication 타입을 지정해야 한다.
     * 여기서는 UsernamePasswordAuthenticationToken 타입만 처리하도록 함.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("AuthenticationProvider 진입 성공");
        log.info("로그인 한 사용자 검증 과정");
        return authOfAfterLogin((JwtUserInfo) authentication.getPrincipal());
    }

    /**
     * JWT 인증 흐름:
     * - 토큰 안에 들어있는 userId를 기반으로 DB에서 사용자를 조회한다.
     * - 조회 성공 시 UserPrincipal(UserDetails 구현체) 생성
     * - 최종적으로 인증 완료(Authentication) 객체 반환
     */
    private Authentication authOfAfterLogin(JwtUserInfo userInfo) {
        UserPrincipal userPrincipal = customUserDetailService.loadUserById(userInfo.userId());
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
