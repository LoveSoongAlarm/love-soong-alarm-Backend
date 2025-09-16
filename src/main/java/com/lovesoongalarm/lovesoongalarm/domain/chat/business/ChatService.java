package com.lovesoongalarm.lovesoongalarm.domain.chat.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.WebSocketMessageService;
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
    private final WebSocketMessageService webSocketMessageService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    public void registerSession(Long userId, String userNickname, WebSocketSession session) {
        log.info("사용자 연결 시작 - userId: {}, sessionId: {}", userId, session.getId());
        sessionService.addSession(userId, session);
        webSocketMessageService.sendConnectionSuccessMessage(userId, userNickname, session);
        log.info("사용자 연결 완료 - userId: {}, sessionId: {}", userId, session.getId());
    }

    public void removeSession(Long userId) {
        sessionService.removeSession(userId);
    }

    public void handleSubscribe(WebSocketSession session, Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.subscribeToChatRoom(session, chatRoomId, userId);
        log.info("채팅방 구독 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void handleUnsubscribe(WebSocketSession session, Long chatRoomId, Long userId) {
        log.info("채팅방 구독 해제 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.unsubscribeToChatRoom(session, chatRoomId, userId);
        log.info("채팅방 구독 해제 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void handleSendMessage(WebSocketSession session, Long chatRoomId, String content, Long userId) {
        log.info("메시지 송신 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.validateChatRoomAccess(chatRoomId, userId);
        messageService.validateMessage(content);
        log.info("메시지 송신 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }
}
