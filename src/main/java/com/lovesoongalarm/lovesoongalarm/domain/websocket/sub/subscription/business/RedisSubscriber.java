package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.SUBSCRIPTION_TTL;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.CHAT_LIST_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private final RedisChatRoomSaver redisChatRoomSaver;
    private final RedisChatRoomRemover redisChatRoomRemover;
    private final RedisChatRoomRetriever redisChatRoomRetriever;
    private final RedisUserChatSaver redisUserChatSaver;
    private final RedisUserChatRemover redisUserChatRemover;
    private final RedisUserChatRetriever redisUserChatRetriever;
    private final RedisChatListSaver redisChatListSaver;
    private final RedisChatListRemover redisChatListRemover;
    private final RedisChatListRetriever redisChatListRetriever;

    public void addSubscriber(Long chatRoomId, Long userId) {
        redisChatRoomSaver.addSubscriber(chatRoomId, userId);
    }

    public void removeSubscriber(Long chatRoomId, Long userId) {
        redisChatRoomRemover.removeSubscriber(chatRoomId, userId);
    }

    public boolean isUserSubscribed(Long chatRoomId, Long userId) {
        return redisChatRoomRetriever.isUserSubscribed(chatRoomId, userId);
    }

    public void subscribeToUserChatUpdates(Long userId) {
        redisUserChatSaver.subscribeToUserChatUpdates(userId);
    }

    public void unsubscribeFromUserChatUpdates(Long userId) {
        redisUserChatRemover.unsubscribeFromUserChatUpdates(userId);
    }

    public boolean isUserSubscribed(Long userId) {
        return redisUserChatRetriever.isUserSubscribed(userId);
    }

    public void subscribeToChatList(Long userId) {
        redisChatListSaver.subscribeToChatList(userId);
    }

    public void unsubscribeToChatList(Long userId) {
        redisChatListRemover.unsubscribeFromChatList(userId);
    }

    public boolean isChatListSubscribed(Long userId) {
        return redisChatListRetriever.isChatListSubscribed(userId);
    }
}
