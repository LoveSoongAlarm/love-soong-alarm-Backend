package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.SUBSCRIPTION_TTL;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.CHAT_LIST_SUBSCRIBERS_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisChatListSaver {

    private final StringRedisTemplate stringRedisTemplate;

    public void subscribeToChatList(Long userId) {
        try {
            String subscribersKey = CHAT_LIST_SUBSCRIBERS_KEY + userId;
            stringRedisTemplate.opsForSet().add(subscribersKey, userId.toString());
            stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);

            log.info("채팅방 목록 구독 시작 - userId: {}", userId);
        } catch (Exception e) {
            log.error("채팅방 목록 구독 실패 - userId: {}", userId, e);
        }
    }


}
