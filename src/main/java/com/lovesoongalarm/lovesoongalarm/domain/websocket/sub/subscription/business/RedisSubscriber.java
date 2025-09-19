package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_CHAT_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisChatRoomSaver redisChatRoomSaver;
    private final RedisChatRoomRemover redisChatRoomRemover;
    private final RedisChatRoomRetriever redisChatRoomRetriever;
    private final RedisUserChatSaver redisUserChatSaver;
    private final RedisUserChatRemover redisUserChatRemover;

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
        try {
            String subscribersKey = USER_CHAT_SUBSCRIBERS_KEY + userId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(subscribersKey, userId.toString());

            boolean isSubscribed = Boolean.TRUE.equals(isMember);
            if (isSubscribed) {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }
            log.debug("사용자 구독 상태 확인 - userId: {}, isSubscribed: {}", userId, isSubscribed);
            return isSubscribed;
        } catch (Exception e) {
            log.error("사용자 구독 상태 체크 실패 - userId: {}", userId, e);
            return false;
        }
    }
}
