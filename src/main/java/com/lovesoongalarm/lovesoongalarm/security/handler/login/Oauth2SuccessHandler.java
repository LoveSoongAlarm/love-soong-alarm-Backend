package com.lovesoongalarm.lovesoongalarm.security.handler.login;

import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;
import com.lovesoongalarm.lovesoongalarm.security.dto.JwtDTO;
import com.lovesoongalarm.lovesoongalarm.security.info.UserPrincipal;
import com.lovesoongalarm.lovesoongalarm.security.service.RefreshTokenService;
import com.lovesoongalarm.lovesoongalarm.utils.CookieUtil;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    @Value("${server.domain}")
    private String domain;
    private final JwtUtil jwtUtil;
    private final HttpSession session;

    @Value("${jwt.redirect}")
    private String REDIRECT_URI;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        JwtDTO jwtDto = jwtUtil.generateTokens(principal.getUserId(), principal.getRole());

        refreshTokenService.updateRefreshToken(principal.getUserId(), jwtDto.refreshToken());

        boolean isRegistered = false;
        if (principal.getStatus() != null && principal.getStatus() == EUserStatus.ACTIVE) {
            isRegistered = true;
        }

        String accessToken = jwtDto.accessToken();
        String refreshToken = jwtDto.refreshToken();

        // refreshToken은 쿠키에 심고
        CookieUtil.addSecureCookie(
                response,
                domain,
                Constants.REFRESH_COOKIE_NAME,
                refreshToken,
                jwtUtil.getRefreshExpiration()
        );

        String redirectUri = String.format(REDIRECT_URI, isRegistered, accessToken);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
