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
public class RedisChatListRetriever {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean isChatListSubscribed(Long userId) {
        try {
            String subscribersKey = CHAT_LIST_SUBSCRIBERS_KEY + userId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(subscribersKey, userId.toString());

            boolean isSubscribed = Boolean.TRUE.equals(isMember);
            if (isSubscribed) {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }
            log.debug("채팅방 목록 구독 상태 확인 - userId: {}, isSubscribed: {}", userId, isSubscribed);
            return isSubscribed;
        } catch (Exception e) {
            log.error("채팅방 목록 구독 상태 체크 실패 - userId: {}", userId, e);
            return false;
        }
    }
}
