package com.lovesoongalarm.lovesoongalarm.security.filter;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.security.info.JwtUserInfo;
import com.lovesoongalarm.lovesoongalarm.security.provider.JwtAuthenticationManager;
import com.lovesoongalarm.lovesoongalarm.utils.HeaderUtil;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return Constants.NO_NEED_AUTH.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, requestURI));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = HeaderUtil.refineHeader(request, Constants.PREFIX_AUTH, Constants.PREFIX_BEARER)
                .orElseThrow(() -> CustomException.type(GlobalErrorCode.INVALID_HEADER_VALUE));

        Claims claims = jwtUtil.validateToken(token);
        log.info("claim: getUserId() = {}", claims.get(Constants.CLAIM_USER_ID, Long.class));

        JwtUserInfo jwtUserInfo = JwtUserInfo.of(
                claims.get(Constants.CLAIM_USER_ID, Long.class),
                ERole.valueOf(claims.get(Constants.CLAIM_USER_ROLE, String.class))
        );

        UsernamePasswordAuthenticationToken unAuthenticatedToken =
                new UsernamePasswordAuthenticationToken(jwtUserInfo, null, null);

        UsernamePasswordAuthenticationToken authenticatedToken =
                (UsernamePasswordAuthenticationToken) jwtAuthenticationManager.authenticate(unAuthenticatedToken);
        log.info("인증 성공");

        authenticatedToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticatedToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}