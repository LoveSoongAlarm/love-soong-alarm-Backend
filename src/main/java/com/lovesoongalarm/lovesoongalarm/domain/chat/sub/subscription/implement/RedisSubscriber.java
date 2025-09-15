package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String CHAT_ROOM_SUBSCRIBERS = "chatroom:subscribers:";

    private static final Duration SUBSCRIPTION_TTL = Duration.ofHours(2);

    public void addSubscriber(Long chatRoomId, Long userId) {
        try {
            String subscribersKey = CHAT_ROOM_SUBSCRIBERS + chatRoomId;
            stringRedisTemplate.opsForSet().add(subscribersKey, userId.toString());
            stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);

            log.info("Redis 구독 추가 - 채팅방: {}, 유저: {}", chatRoomId, userId);

        } catch (Exception e) {
            log.error("Redis 구독 추가 실패 - 채팅방: {}, 유저: {}", chatRoomId, userId, e);
        }
    }

    public boolean isUserSubscribed(Long chatRoomId, Long userId) {
        try {
            String subscribersKey = CHAT_ROOM_SUBSCRIBERS + chatRoomId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(subscribersKey, userId.toString());

            boolean isSubscribed = Boolean.TRUE.equals(isMember);
            return isSubscribed;
        } catch (Exception e) {
            log.error("Redis 구독 상태 체크 실패 - 채팅방: {}, 유저: {}", chatRoomId, userId, e);
            return false;
        }
    }
}
