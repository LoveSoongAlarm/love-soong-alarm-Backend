package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.business.MessageReadService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final RedisSubscriber redisSubscriber;
    private final MessageReadService messageReadService;

    @Transactional
    public void subscribeToChatRoom(Long chatRoomId, Long userId) {
        redisSubscriber.addSubscriber(chatRoomId, userId);
        messageReadService.processAutoReadOnSubscribe(chatRoomId, userId);
    }
}
