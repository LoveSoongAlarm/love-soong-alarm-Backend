package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final RedisSubscriber redisSubscriber;
    private final SessionService sessionService;
    private final MessageSender messageSender;

    public void publishUnreadBadgeUpdate(Long userId, int totalUnreadCount) {
        try {
            if (!redisSubscriber.isUserSubscribed(userId)) {
                log.debug("배지 업데이트 구독하지 않은 사용자 - userId: {}", userId);
                return;
            }

            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendUnreadBadgeUpdate(session, totalUnreadCount);
            log.info("안 읽은 메시지 배지 업데이트 전송 완료 - userId: {}, count: {}", userId, totalUnreadCount);

        } catch (Exception e) {
            log.error("안 읽은 메시지 배지 업데이트 발행 실패 - userId: {}", userId, e);
        }
    }

    public void publishUserChatUpdate(Long chatRoomId, Long userId, UserChatUpdateDTO updateEvent) {
        try {
            if (!redisSubscriber.isChatListSubscribed(userId)) {
                log.debug("목록을 구독하지 않은 사용자에게는 업데이트를 보내지 않음 - userId: {}", userId);
                return;
            }

            if (redisSubscriber.isUserSubscribed(chatRoomId, userId)) {
                log.debug("채팅방을 구독중인 사용자에게는 업데이트를 보내지 않음 - userId: {}", userId);
                return;
            }

            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendChatListUpdate(session, updateEvent);
            log.info("사용자 채팅 업데이트 전송 완료 - userId: {}, chatRoomId: {}", userId, updateEvent.chatRoomId());

        } catch (Exception e) {
            log.error("사용자 채팅 업데이트 발행 실패 - userId: {}", userId, e);
        }
    }

    public void publishNewChatRoomNotification(Long userId, Long chatRoomId, String partnerNickname, String partnerEmoji) {
        try {
            if (!redisSubscriber.isChatListSubscribed(userId)) {
                log.debug("채팅방 목록을 구독하지 않은 사용자에게는 새 채팅방 알림을 보내지 않음 - userId: {}", userId);
                return;
            }

            WebSocketSession session = sessionService.getSession(userId);
            if (session == null || !session.isOpen()) {
                log.debug("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            messageSender.sendNewChatRoomNotification(session, chatRoomId, partnerNickname, partnerEmoji);
            log.info("새 채팅방 알림 전송 완료 - userId: {}, chatRoomId: {}, partner: {}", userId, chatRoomId, partnerNickname);

        } catch (Exception e) {
            log.error("새 채팅방 알림 발행 실패 - userId: {}, chatRoomId: {}", userId, chatRoomId, e);
        }
    }
}
