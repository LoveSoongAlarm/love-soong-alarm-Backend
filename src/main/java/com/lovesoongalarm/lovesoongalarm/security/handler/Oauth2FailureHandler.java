package com.lovesoongalarm.lovesoongalarm.security.handler;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.security.info.AuthenticationResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Oauth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        AuthenticationResponse.makeFailureResponse(response, GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}
