package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.SUBSCRIPTION_TTL;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_CHAT_SUBSCRIBERS_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUserChatSaver {

    private final StringRedisTemplate stringRedisTemplate;

    public void subscribeToChatBadgeUpdate(Long userId) {
        try {
            String subscribersKey = USER_CHAT_SUBSCRIBERS_KEY + userId;

            stringRedisTemplate.opsForSet().add(subscribersKey, userId.toString());
            stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);

            log.info("사용자 채팅 업데이트 구독 시작 - userId: {}", userId);
        } catch (Exception e) {
            log.error("사용자 채팅 업데이트 구독 실패 - userId: {}", userId, e);
        }
    }
}
