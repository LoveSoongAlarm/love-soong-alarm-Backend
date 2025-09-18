package com.lovesoongalarm.lovesoongalarm.domain.websocket.interceptor;

import com.lovesoongalarm.lovesoongalarm.common.constant.Constants;
import com.lovesoongalarm.lovesoongalarm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketJWTAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("JWT 기반 WebSocket 인증 시작");

        try {
            String token = extractTokenFromQuery(request);

            if (token == null) {
                log.error("JWT 토큰이 없습니다");
                return false;
            }

            Claims claims = jwtUtil.validateToken(token);
            Long userId = claims.get(Constants.CLAIM_USER_ID, Long.class);
            if (userId == null) {
                log.error("JWT 토큰에서 userId를 찾을 수 없습니다");
                return false;
            }

            attributes.put("userId", userId);

            log.info("JWT 인증 성공 - userId: {}", userId);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT 토큰 형식이 잘못되었습니다: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT 토큰 서명이 유효하지 않습니다: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 알 수 없는 오류 발생", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake 중 오류 발생", exception);
        }
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        String query = request.getURI().getQuery();

        if (query == null) {
            return null;
        }

        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }

        return null;
    }
}
