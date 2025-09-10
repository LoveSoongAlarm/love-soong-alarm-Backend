package com.lovesoongalarm.lovesoongalarm.common.config;

import com.lovesoongalarm.lovesoongalarm.domain.chat.handler.WebSocketChatHandler;
import com.lovesoongalarm.lovesoongalarm.domain.chat.interceptor.WebSocketJWTAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketChatHandler webSocketChatHandler;
    private final WebSocketJWTAuthInterceptor webSocketJWTAuthInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(webSocketChatHandler, "/ws/chats")
                .addInterceptors(webSocketJWTAuthInterceptor)
                .setAllowedOrigins("http://localhost:5173", "https://love-soong-alarm-web.vercel.app/")
                .withSockJS();
    }
}
