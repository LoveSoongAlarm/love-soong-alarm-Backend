package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final RedisSubscriber redisSubscriber;

    public void subscribeToChatRoom(Long chatRoomId, Long userId) {
        redisSubscriber.addSubscriber(chatRoomId, userId);
        log.info("채팅방 구독 - 채팅방: {}, 사용자: {}", chatRoomId, userId);
    }
}
