package com.lovesoongalarm.lovesoongalarm.domain.chat.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class WebSocketJWTAuthInterceptor implements HandshakeInterceptor {
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

            // TODO - JWT 인증 로직 추가 및 JWT에서 userId 가져오기

            Long userId = Long.parseLong(token);
            attributes.put("userId", userId);

            log.info("JWT 인증 성공 - userId: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생", e);
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
