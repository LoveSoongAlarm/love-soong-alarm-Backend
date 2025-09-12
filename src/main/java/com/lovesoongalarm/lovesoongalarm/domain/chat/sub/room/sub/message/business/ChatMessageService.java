package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessageSender messageSender;

    @Transactional
    public void sendConnectionSuccessMessage(Long userId, String userNickname, WebSocketSession session) {
        WebSocketMessageDTO.ConnectionInfo connectionInfo = WebSocketMessageDTO.ConnectionInfo.builder()
                .type(EWebSocketMessageType.CONNECTION_SUCCESS)
                .userId(userId)
                .userNickname(userNickname)
                .timestamp(LocalDateTime.now())
                .message("WebSocket 연결이 성공했습니다.")
                .build();

        messageSender.sendMessage(session, connectionInfo);
    }
}
