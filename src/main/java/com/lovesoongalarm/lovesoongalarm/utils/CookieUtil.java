package com.lovesoongalarm.lovesoongalarm.utils;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

@Slf4j
public class CookieUtil {

    public static String getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            throw new CustomException(GlobalErrorCode.INVALID_TOKEN_ERROR);
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CustomException(GlobalErrorCode.INVALID_TOKEN_ERROR));
    }

    public static void addCookie(
            HttpServletResponse response,
            String domain,
            String key,
            String value
    ) {
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .domain(domain)
                .httpOnly(false)
                .secure(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void logoutCookie(
            HttpServletResponse response,
            String domain
    ) {
        ResponseCookie expiredCookie = ResponseCookie.from(Constants.REFRESH_COOKIE_NAME, "")
                .path("/")
                .domain(domain)
                .secure(true)       // HTTPS 필수
                .httpOnly(true)     // Refresh Token은 보안상 HttpOnly
                .maxAge(0)          // 즉시 만료
                .sameSite("None")   // 크로스 도메인 로그인/로그아웃 지원
                .build();

        log.info("[Cookie] Logout cookie set: {}", expiredCookie.toString());

        response.addHeader("Set-Cookie", expiredCookie.toString());
    }


    public static void addSecureCookie(
            HttpServletResponse response,
            String domain,
            String key,
            String value,
            Integer maxAge
    ) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return;

        for (Cookie cookie : cookies)
            if (cookie.getName().equals(name)) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
    }
}
