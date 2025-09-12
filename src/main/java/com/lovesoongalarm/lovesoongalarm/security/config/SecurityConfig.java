package com.lovesoongalarm.lovesoongalarm.security.config;

import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.security.filter.JwtAuthenticationFilter;
import com.lovesoongalarm.lovesoongalarm.security.filter.JwtExceptionFilter;
import com.lovesoongalarm.lovesoongalarm.security.handler.Oauth2FailureHandler;
import com.lovesoongalarm.lovesoongalarm.security.handler.Oauth2SuccessHandler;
import com.lovesoongalarm.lovesoongalarm.security.handler.exception.CustomAccessDeniedHandler;
import com.lovesoongalarm.lovesoongalarm.security.handler.exception.CustomAuthenticationEntryPointHandler;
import com.lovesoongalarm.lovesoongalarm.security.provider.JwtAuthenticationManager;
import com.lovesoongalarm.lovesoongalarm.security.service.CustomOauth2UserDetailService;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;
    private final CustomOauth2UserDetailService customOauth2UserDetailService;
    //private final CustomLogoutProcessHandler customLogoutProcessHandler;
    //private final CustomLogoutResultHandler customLogoutResultHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(Constants.NO_NEED_AUTH.toArray(String[]::new)).permitAll()
                                .requestMatchers("/api/**").hasAnyRole("USER")
                                .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2SuccessHandler) //로그인 성공 시
                        .failureHandler(oauth2FailureHandler) // 로그인 실패 시
                        .userInfoEndpoint(it -> it.userService(customOauth2UserDetailService)) //사용자 정보 조회 시
                ) //로그인 후, Authorization Code → Access Token 교환까지 끝난 직후

                /*.logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(customLogoutProcessHandler)
                        .logoutSuccessHandler(customLogoutResultHandler)
                )*/
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler) //권한 부족(403)
                        .authenticationEntryPoint(customAuthenticationEntryPointHandler) //인증 실패(401)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, jwtAuthenticationManager), LogoutFilter.class //JWT 토큰을 검사 → 인증 처리
                )
                .addFilterBefore(
                        new JwtExceptionFilter(), JwtAuthenticationFilter.class //JWT 인증 중 에러 핸들러
                )
                .getOrBuild();
    }
}