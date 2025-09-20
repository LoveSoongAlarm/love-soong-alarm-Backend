package com.lovesoongalarm.lovesoongalarm.domain.websocket.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final SubscriptionService subscriptionService;

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

    public void subscribeToChatList(WebSocketSession session, Long userId) {
        log.info("채팅방 목록 구독 시작 - userId: {}", userId);
        subscriptionService.subscribeToChatList(session, userId);
        log.info("채팅방 목록 구독 완료 - userId: {}", userId);
    }

    public void unsubscribeFromChatList(WebSocketSession session, Long userId) {
        log.info("채팅방 목록 구독 해제 시작 - userId: {}", userId);
        subscriptionService.unsubscribeFromChatList(session, userId);
        log.info("채팅방 목록 구독 해제 완료 - userId: {}", userId);
    }

    @Transactional
    public void handleSendMessage(Long chatRoomId, String content, Long userId) {
        log.info("메시지 송신 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.validateChatRoomAccess(userId, chatRoomId);
        ChatRoom chatRoom = chatRoomService.getChatRoomOrElseThrow(chatRoomId);
        messageService.sendMessage(chatRoom, content, userId);
        log.info("메시지 송신 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }
}
