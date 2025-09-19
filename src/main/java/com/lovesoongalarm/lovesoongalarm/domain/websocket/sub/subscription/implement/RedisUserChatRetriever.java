package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.SUBSCRIPTION_TTL;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_CHAT_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUserChatRetriever {

    private final StringRedisTemplate stringRedisTemplate;

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
