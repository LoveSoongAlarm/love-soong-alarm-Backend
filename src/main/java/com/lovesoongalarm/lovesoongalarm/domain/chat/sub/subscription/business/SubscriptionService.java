package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageReadService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.UnreadCountService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.WebSocketMessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
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
    private final WebSocketMessageService webSocketMessageService;
    private final UnreadCountService unreadCountService;

    @Transactional
    public void subscribeToChatRoom(WebSocketSession session, Long chatRoomId, Long userId) {
        messageReadService.processAutoReadOnSubscribe(chatRoomId, userId);
        redisSubscriber.addSubscriber(chatRoomId, userId);
        webSocketMessageService.sendSubscribeSuccessMessage(session, chatRoomId);
    }

    public void unsubscribeToChatRoom(WebSocketSession session, Long chatRoomId, Long userId) {
        redisSubscriber.removeSubscriber(chatRoomId, userId);
        webSocketMessageService.sendUnsubscribeSuccessMessage(session, chatRoomId);
    }

    public void subscribeToUserChatUpdates(WebSocketSession session, Long userId){
        redisSubscriber.subscribeToUserChatUpdates(userId);

        int totalUnreadCount = unreadCountService.getTotalUnreadCount(userId);
        webSocketMessageService.sendUnreadBadgeUpdate(session, totalUnreadCount);
    }

    public void unsubscribeFromUserChatUpdates(Long userId){
        redisSubscriber.unsubscribeFromUserChatUpdates(userId);
    }
}
