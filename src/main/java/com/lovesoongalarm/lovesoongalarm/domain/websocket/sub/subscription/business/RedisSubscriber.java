package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.RedisChatRoomRemover;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.RedisChatRoomSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.CHAT_ROOM_SUBSCRIBERS_KEY;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.USER_CHAT_SUBSCRIBERS_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private static final Duration SUBSCRIPTION_TTL = Duration.ofHours(24);

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisChatRoomSaver redisChatRoomSaver;
    private final RedisChatRoomRemover redisChatRoomRemover;

    public void addSubscriber(Long chatRoomId, Long userId) {
        redisChatRoomSaver.addSubscriber(chatRoomId, userId);
    }

    public void removeSubscriber(Long chatRoomId, Long userId) {
        redisChatRoomRemover.removeSubscriber(chatRoomId, userId);
    }

    public boolean isUserSubscribed(Long chatRoomId, Long userId) {
        try {
            String subscribersKey = CHAT_ROOM_SUBSCRIBERS_KEY + chatRoomId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(subscribersKey, userId.toString());

            boolean isSubscribed = Boolean.TRUE.equals(isMember);
            if (isSubscribed) {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }
            log.debug("채팅방 구독 상태 확인 - chatRoomId: {}, userId: {}, isSubscribed: {}", chatRoomId, userId, isSubscribed);
            return isSubscribed;
        } catch (Exception e) {
            log.error("Redis 구독 상태 체크 실패 - 채팅방: {}, 유저: {}", chatRoomId, userId, e);
            return false;
        }
    }

    public void subscribeToUserChatUpdates(Long userId) {
        try {
            String subscribersKey = USER_CHAT_SUBSCRIBERS_KEY + userId;

            stringRedisTemplate.opsForSet().add(subscribersKey, userId.toString());
            stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);

            log.info("사용자 채팅 업데이트 구독 시작 - userId: {}", userId);
        } catch (Exception e) {
            log.error("사용자 채팅 업데이트 구독 실패 - userId: {}", userId, e);
        }
    }

    public void unsubscribeFromUserChatUpdates(Long userId) {
        try {
            String subscribersKey = USER_CHAT_SUBSCRIBERS_KEY + userId;
            stringRedisTemplate.opsForSet().remove(subscribersKey, userId.toString());

            Long remainingCount = stringRedisTemplate.opsForSet().size(subscribersKey);
            if (remainingCount != null && remainingCount == 0) {
                stringRedisTemplate.delete(subscribersKey);
                log.debug("빈 구독 키 삭제 - userId: {}", userId);
            } else {
                stringRedisTemplate.expire(subscribersKey, SUBSCRIPTION_TTL);
            }

            log.info("사용자 채팅 업데이트 구독 해제 - userId: {}", userId);
        } catch (Exception e) {
            log.error("사용자 채팅 업데이트 구독 해제 실패 - userId: {}", userId, e);
        }
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
