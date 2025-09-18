package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.WebSocketMessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserChatSubscriptionService {

    private final RedisSubscriber redisSubscriber;

    private final ChatSessionService chatSessionService;
    private final WebSocketMessageService webSocketMessageService;

    public void publishUnreadBadgeUpdate(Long userId, int totalUnreadCount) {
        try {
            if (!redisSubscriber.isUserSubscribed(userId)) {
                return;
            }

            WebSocketSession session = chatSessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            webSocketMessageService.sendUnreadBadgeUpdate(session, totalUnreadCount);
            log.info("안 읽은 메시지 배지 업데이트 전송 완료 - userId: {}, count: {}", userId, totalUnreadCount);

        } catch (Exception e) {
            log.error("안 읽은 메시지 배지 업데이트 발행 실패 - userId: {}", userId, e);
        }
    }

    public void publishUserChatUpdate(Long userId, UserChatUpdateDTO updateEvent) {
        try {
            if (!redisSubscriber.isUserSubscribed(userId)) {
                log.debug("구독하지 않은 사용자에게는 업데이트를 보내지 않음 - userId: {}", userId);
                return;
            }

            WebSocketSession session = chatSessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            webSocketMessageService.sendChatListUpdate(session, updateEvent);
            log.info("사용자 채팅 업데이트 전송 완료 - userId: {}, chatRoomId: {}", userId, updateEvent.chatRoomId());

        } catch (Exception e) {
            log.error("사용자 채팅 업데이트 발행 실패 - userId: {}", userId, e);
        }
    }
}
