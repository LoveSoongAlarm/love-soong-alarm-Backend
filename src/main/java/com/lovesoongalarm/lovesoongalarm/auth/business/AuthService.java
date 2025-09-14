package com.lovesoongalarm.lovesoongalarm.auth.business;

import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.auth.application.dto.ReissueTokenResponseDTO;
import com.lovesoongalarm.lovesoongalarm.security.dto.JwtDTO;
import com.lovesoongalarm.lovesoongalarm.security.service.RefreshTokenService;
import com.lovesoongalarm.lovesoongalarm.utils.CookieUtil;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${server.domain}")
    private String domain;

    public ReissueTokenResponseDTO reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = CookieUtil.getCookie(request, Constants.REFRESH_COOKIE_NAME);
        System.out.println("#"+refreshToken);
        Long userId = jwtUtil.validateRefreshToken(refreshToken);

        System.out.println("#"+userId);

        JwtDTO jwtDto = jwtUtil.generateTokens(userId, ERole.USER);

        refreshTokenService.updateRefreshToken(userId, jwtDto.refreshToken());

        CookieUtil.logoutCookie(response, domain);

        // refreshToken은 쿠키에 심고
        CookieUtil.addSecureCookie(
                response,
                domain,
                Constants.REFRESH_COOKIE_NAME,
                jwtDto.refreshToken(),
                jwtUtil.getRefreshExpiration()
        );

        return ReissueTokenResponseDTO.from(jwtDto.accessToken());
    }
}
