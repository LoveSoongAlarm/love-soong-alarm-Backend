package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.session.SessionService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.UserSubscriptionService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.implement.RedisSubscriber;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReadService {

    private final RedisSubscriber redisSubscriber;
    private final MessageUpdater messageUpdater;
    private final MessageRetriever messageRetriever;


    private final UserService userService;
    private final SessionService sessionService;
    private final MessageSender messageSender;
    private final UnreadCountService unreadCountService;
    private final UserSubscriptionService userSubscriptionService;


    public void processAutoReadOnSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시 자동읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            int updatedCount = messageUpdater.markMessagesAsReadByChatRoomAndReceiver(chatRoomId, userId);
            if (updatedCount == 0) {
                log.info("읽음 처리할 메시지가 없으므로 자동읽음 처리 종료 - chatRoomId: {}, userId: {}", chatRoomId, userId);
                return;
            }

            User partner = userService.getPartnerUser(chatRoomId, userId);
            notifyReadStatusUpdate(chatRoomId, userId, partner.getId());

            handleChatListUpdateOnRead(chatRoomId, userId, partner, updatedCount);

            log.info("채팅방 구독 시 자동읽음 처리 완료 - chatRoomId: {}, userId: {}", chatRoomId, userId);
        } catch (Exception e) {
            log.error("자동읽음 처리 중 오류 발생 - chatRoomId: {}, userId: {}", chatRoomId, userId, e);
        }
    }

    @Transactional
    public void processAutoReadOnMessageReceive(Long chatRoomId, Long receiverId, Long messageId) {
        log.info("메시지 수신 시 자동읽음 처리 시작 - chatRoomId: {}, receiverId: {}, messageId: {}",
                chatRoomId, receiverId, messageId);

        try {
            messageUpdater.markAsRead(messageId);

            User partner = userService.getPartnerUser(chatRoomId, receiverId);
            notifyReadStatusUpdate(chatRoomId, receiverId, partner.getId());

            log.info("메시지 수신 시 자동읽음 처리 완료 - messageId: {}", messageId);

        } catch (Exception e) {
            log.error("메시지 수신 시 자동읽음 처리 중 오류 발생 - chatRoomId: {}, receiverId: {}, messageId: {}",
                    chatRoomId, receiverId, messageId, e);
        }
    }

    private void notifyReadStatusUpdate(Long chatRoomId, Long readerId, Long partnerId) {
        log.info("읽음 상태 알림 - chatRoomId: {}, readerId: {}, partnerId: {}", chatRoomId, readerId, partnerId);

        if (!redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) return;

        WebSocketSession partnerSession = sessionService.getSession(partnerId);
        if (partnerSession != null && partnerSession.isOpen()) {
            messageSender.sendReadMessage(partnerSession, chatRoomId, readerId);
            log.info("읽음 상태 알림 완료 - partnerId: {}", partnerId);
        } else {
            log.debug("상대방의 세션이 없거나 닫혀있어 읽음 알림을 보내지 않음 - partnerId: {}", partnerId);
        }
    }

    private void handleChatListUpdateOnRead(Long chatRoomId, Long userId, User partner, int readCount) {
        try {
            Optional<Message> lastMessage = messageRetriever.findLastMessageByChatRoomId(chatRoomId);
            if (lastMessage.isEmpty()) {
                log.debug("마지막 메시지가 없어서 채팅방 목록 업데이트를 건너뜀 - chatRoomId: {}", chatRoomId);
                return;
            }
            Message message = lastMessage.get();
            notifyPartnerOfReadStatus(chatRoomId, partner.getId(), message);

            int updatedUnreadCount = unreadCountService.getTotalUnreadCount(userId);
            userSubscriptionService.publishUnreadBadgeUpdate(userId, updatedUnreadCount);

            log.info("읽음 처리 시 채팅방 목록 업데이트 완료 - userId: {}, chatRoomId: {}, readCount: {}, newUnreadCount: {}",
                    userId, chatRoomId, readCount, updatedUnreadCount);

        } catch (Exception e) {
            log.error("읽음 처리 시 채팅방 목록 업데이트 실패 - userId: {}, chatRoomId: {}", userId, chatRoomId, e);
        }
    }

    private void notifyPartnerOfReadStatus(Long chatRoomId, Long partnerId, Message lastMessage) {
        try {
            boolean isPartnerMessage = lastMessage.getUser().getId().equals(partnerId);

            if (isPartnerMessage) {
                UserChatUpdateDTO partnerUpdate = UserChatUpdateDTO.builder()
                        .chatRoomId(chatRoomId)
                        .lastMessageContent(lastMessage.getContent())
                        .timestamp(lastMessage.getCreatedAt())
                        .isMyMessage(true)
                        .isRead(lastMessage.isRead())
                        .build();

                userSubscriptionService.publishUserChatUpdate(partnerId, partnerUpdate);
                log.debug("상대방에게 읽음 상태 업데이트 알림 전송 - partnerId: {}, chatRoomId: {}", partnerId, chatRoomId);
            }

        } catch (Exception e) {
            log.error("상대방 읽음 상태 알림 실패 - partnerId: {}, chatRoomId: {}", partnerId, chatRoomId, e);
        }
    }
}
