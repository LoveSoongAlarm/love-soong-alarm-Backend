package com.lovesoongalarm.lovesoongalarm.security.handler.logout;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.security.service.RefreshTokenService;
import com.lovesoongalarm.lovesoongalarm.utils.HeaderUtil;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutProcessHandler implements LogoutHandler {
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            throw CustomException.type(GlobalErrorCode.UNAUTHORIZED);
        }

        String accessToken = HeaderUtil.refineHeader(request, Constants.PREFIX_AUTH, Constants.PREFIX_BEARER)
                .orElseThrow(() -> CustomException.type(GlobalErrorCode.INVALID_HEADER_VALUE));

        Claims claims = jwtUtil.validateToken(accessToken);
        refreshTokenService.deleteRefreshToken(claims.get(Constants.CLAIM_USER_ID, Long.class));
    }
}
