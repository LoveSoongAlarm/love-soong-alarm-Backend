package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.SUBSCRIPTION_TTL;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.CHAT_ROOM_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatRoomRemover {

    private final StringRedisTemplate stringRedisTemplate;

    public void removeSubscriber(Long chatRoomId, Long userId) {
        try {
            String subscribersKey = CHAT_ROOM_SUBSCRIBERS_KEY + chatRoomId;
            stringRedisTemplate.opsForSet().remove(subscribersKey, userId.toString());

            Long remainingCount = stringRedisTemplate.opsForSet().size(subscribersKey);
            if (remainingCount != null && remainingCount == 0) {
                stringRedisTemplate.delete(subscribersKey);
                log.debug("빈 구독 키 삭제 - 채팅방: {}", chatRoomId);
            } else {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }

            log.info("Redis 구독 제거 - 채팅방: {}, 멤버: {}", chatRoomId, userId);

        } catch (Exception e) {
            log.error("Redis 구독 제거 실패 - 채팅방: {}, 멤버: {}", chatRoomId, userId, e);
        }
    }
}
