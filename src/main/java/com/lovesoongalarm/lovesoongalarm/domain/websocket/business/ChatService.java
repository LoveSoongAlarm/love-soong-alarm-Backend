package com.lovesoongalarm.lovesoongalarm.domain.websocket.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomBlockService;
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
    private final ChatRoomBlockService chatRoomBlockService;

    public void handleSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.subscribeToChatRoom(chatRoomId, userId);
        log.info("채팅방 구독 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void handleUnsubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 해제 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.unsubscribeToChatRoom(chatRoomId, userId);
        log.info("채팅방 구독 해제 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void subscribeToChatList(Long userId) {
        log.info("채팅방 목록 구독 시작 - userId: {}", userId);
        subscriptionService.subscribeToChatList(userId);
        log.info("채팅방 목록 구독 완료 - userId: {}", userId);
    }

    public void unsubscribeFromChatList(Long userId) {
        log.info("채팅방 목록 구독 해제 시작 - userId: {}", userId);
        subscriptionService.unsubscribeFromChatList(userId);
        log.info("채팅방 목록 구독 해제 완료 - userId: {}", userId);
    }

    @Transactional
    public void handleSendMessage(WebSocketSession session, Long chatRoomId, String content, Long userId) {
        log.info("메시지 송신 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomService.validateChatRoomAccess(userId, chatRoomId);
        ChatRoom chatRoom = chatRoomService.getChatRoomOrElseThrow(chatRoomId);
        messageService.sendMessage(session, chatRoom, content, userId);
        log.info("메시지 송신 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void blockUserInChatRoom(Long userId, Long chatRoomId) {
        log.info("사용자 차단 시작 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        chatRoomBlockService.blockUserInChatRoom(userId, chatRoomId);
        log.info("사용자 차단 완료 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }

    public void unblockUserInChatRoom(Long userId, Long chatRoomId) {
        log.info("사용자 차단 해제 시작 - userId: {}, chatRoomId: {}, targetId: {}", userId, chatRoomId);
        chatRoomBlockService.unblockUserInChatRoom(userId, chatRoomId);
        log.info("사용자 차단 해제 완료 - userId: {}, chatRoomId: {}, targetId: {}", userId, chatRoomId);
    }
}
