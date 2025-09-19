package com.lovesoongalarm.lovesoongalarm.auth.business;

import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserDeleter;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.auth.application.dto.ReissueTokenResponseDTO;
import com.lovesoongalarm.lovesoongalarm.security.dto.JwtDTO;
import com.lovesoongalarm.lovesoongalarm.security.service.OAuthUserInfo;
import com.lovesoongalarm.lovesoongalarm.security.service.RefreshTokenService;
import com.lovesoongalarm.lovesoongalarm.utils.CookieUtil;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRetriever userRetriever;
    private final OAuthUserInfo oAuthUserInfo;
    private final UserDeleter userDeleter;
    private final UserService userService;

    @Value("${server.domain}")
    private String domain;

    @Transactional
    public ReissueTokenResponseDTO reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, Constants.REFRESH_COOKIE_NAME);
        Long userId = jwtUtil.validateRefreshToken(refreshToken);

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

    @Transactional
    public Void withdraw(Long userId, HttpServletResponse response) {
        userService.sweepUserInformation(Long.parseLong("4"));
        User findUser = userRetriever.findByIdAndOnlyActive(userId);
        oAuthUserInfo.revoke(findUser.getPlatform(), findUser.getSerialId());
        CookieUtil.logoutCookie(response, domain);
        refreshTokenService.deleteRefreshToken(userId);
        findUser.softDelete();
        findUser.getInterests().clear();
        //userDeleter.deleteUser(findUser);
        return null;
    }
}
