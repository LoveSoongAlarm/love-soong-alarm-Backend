package com.lovesoongalarm.lovesoongalarm.security.handler.logout;

import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.security.info.AuthenticationResponse;
import com.lovesoongalarm.lovesoongalarm.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutResultHandler implements LogoutSuccessHandler {

    @Value("${server.domain}")
    private String domain;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        if (authentication == null) {
//            log.info("인증 정보가 존재하지 않습니다. authentication is null.");
//            AuthenticationResponse.makeFailureResponse(response, UserErrorCode.USER_NOT_FOUND);
//        }

        CookieUtil.logoutCookie(response, domain);
        AuthenticationResponse.makeSuccessResponse(response);
    }
}
