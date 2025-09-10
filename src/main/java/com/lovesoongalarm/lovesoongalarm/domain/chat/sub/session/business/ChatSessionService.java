package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionService {

    private final Map<Long, WebSocketSession> memberSessions = new ConcurrentHashMap<>();

    @Transactional
    public void addSession(Long userId, WebSocketSession session) {
        memberSessions.put(userId, session);
        log.info("로컬 세션에 user 추가 완료 - userId: {}, sessionId: {}", userId, session.getId());
    }
}
