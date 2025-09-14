package com.lovesoongalarm.lovesoongalarm.common.constant;

import java.util.List;

public class Constants {
    public static String CLAIM_USER_ID = "uuid";
    public static String CLAIM_USER_ROLE = "role";
    public static String PREFIX_BEARER = "Bearer ";
    public static String PREFIX_AUTH = "authorization";
    public static String ACCESS_COOKIE_NAME = "access_token";
    public static String REFRESH_COOKIE_NAME = "refresh_token";
    public static String REFRESH_TOKEN_PREFIX = "RT:";
    public static List<String> NO_NEED_AUTH = List.of(
            "/api/v1/health-check",
            "/swagger",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/oauth2/authorization/kakao",
            "/login/oauth2/code/kakao",
            "/api/auth/reissue",
            "/ws/**"
    );
}
