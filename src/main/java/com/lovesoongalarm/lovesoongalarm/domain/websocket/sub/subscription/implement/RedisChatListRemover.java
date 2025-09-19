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
public class RedisChatListRemover {

    private final StringRedisTemplate stringRedisTemplate;

    public void unsubscribeFromChatList(Long userId) {
        try {
            String subscribersKey = CHAT_LIST_SUBSCRIBERS_KEY + userId;
            stringRedisTemplate.opsForSet().remove(subscribersKey, userId.toString());

            Long remainingCount = stringRedisTemplate.opsForSet().size(subscribersKey);
            if (remainingCount != null && remainingCount == 0) {
                stringRedisTemplate.delete(subscribersKey);
                log.debug("빈 채팅방 목록 구독 키 삭제 - userId: {}", userId);
            } else {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }

            log.info("채팅방 목록 구독 해제 - userId: {}", userId);
        } catch (Exception e) {
            log.error("채팅방 목록 구독 해제 실패 - userId: {}", userId, e);
        }
    }
}
