package com.lovesoongalarm.lovesoongalarm.domain.chat.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionService sessionService;
    private final MessageService messageService;

    public void registerSession(Long userId, String userNickname, WebSocketSession session) {
        log.info("사용자 연결 시작 - userId: {}, sessionId: {}", userId, session.getId());
        sessionService.addSession(userId, session);
        messageService.sendConnectionSuccessMessage(userId, userNickname, session);
        log.info("사용자 연결 완료 - userId: {}, sessionId: {}", userId, session.getId());
    }

    public void removeSession(Long userId) {
        sessionService.removeSession(userId);
    }
}
