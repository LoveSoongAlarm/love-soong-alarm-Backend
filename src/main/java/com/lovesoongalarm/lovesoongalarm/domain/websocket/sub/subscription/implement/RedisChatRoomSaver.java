package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.CHAT_ROOM_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatRoomSaver {

    private final StringRedisTemplate stringRedisTemplate;

    private static final Duration SUBSCRIPTION_TTL = Duration.ofHours(24);

    public void addSubscriber(Long chatRoomId, Long userId) {
        try {
            String subscribersKey = CHAT_ROOM_SUBSCRIBERS_KEY + chatRoomId;
            stringRedisTemplate.opsForSet().add(subscribersKey, userId.toString());
            stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            log.info("Redis 구독 추가 - 채팅방: {}, 유저: {}", chatRoomId, userId);
        } catch (Exception e) {
            log.error("Redis 구독 추가 실패 - 채팅방: {}, 유저: {}", chatRoomId, userId, e);
        }
    }
}
