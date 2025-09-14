package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSender {

    private final ObjectMapper objectMapper;

    public void sendMessage(WebSocketSession session, Object message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            synchronized (session) {
                session.sendMessage(new TextMessage(messageJson));
            }
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
        }
    }
}
