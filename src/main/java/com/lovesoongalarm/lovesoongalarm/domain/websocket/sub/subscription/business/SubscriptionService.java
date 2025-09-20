package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageReadService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.ReadProcessingService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.UnreadCountService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final RedisSubscriber redisSubscriber;
    private final MessageReadService messageReadService;
    private final ReadProcessingService readProcessingService;
    private final MessageSender messageSender;
    private final UnreadCountService unreadCountService;

    @Transactional
    public void subscribeToChatRoom(WebSocketSession session, Long chatRoomId, Long userId) {
        redisSubscriber.addSubscriber(chatRoomId, userId);
        messageSender.sendSubscribeSuccessMessage(session, chatRoomId);
        MessageReadService.ReadResult readResult = messageReadService.markUnreadMessagesAsRead(chatRoomId, userId);
        readProcessingService.handleSubscribeReadResult(readResult);
    }

    public void unsubscribeToChatRoom(WebSocketSession session, Long chatRoomId, Long userId) {
        redisSubscriber.removeSubscriber(chatRoomId, userId);
        messageSender.sendUnsubscribeSuccessMessage(session, chatRoomId);
    }

    public void subscribeToChatBadgeUpdate(WebSocketSession session, Long userId){
        redisSubscriber.subscribeToUserChatUpdates(userId);
        int totalUnreadCount = unreadCountService.getTotalUnreadCount(userId);
        messageSender.sendUnreadBadgeUpdate(session, totalUnreadCount);
    }

    public void unsubscribeFromChatBadgeUpdate(Long userId){
        redisSubscriber.unsubscribeFromUserChatUpdates(userId);
    }

    public void subscribeToChatList(WebSocketSession session, Long userId) {
        redisSubscriber.subscribeToChatList(userId);
        messageSender.sendChatListSubscribeSuccessMessage(session);
    }

    public void unsubscribeFromChatList(WebSocketSession session, Long userId) {
        redisSubscriber.unsubscribeFromChatList(userId);
        messageSender.sendChatListUnsubscribeSuccessMessage(session);
    }
}
