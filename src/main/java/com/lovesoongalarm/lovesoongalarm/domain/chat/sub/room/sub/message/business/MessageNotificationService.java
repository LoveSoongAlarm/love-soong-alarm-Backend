package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business.SubscriptionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageNotificationService {

    private final RedisSubscriber redisSubscriber;

    private final ChatSessionService chatSessionService;
    private final UserService userService;
    private final MessageReadService messageReadService;
    private final WebSocketMessageService webSocketMessageService;
    private final UnreadCountService unreadCountService;

    public void notifyMessage(Long chatRoomId, Message message, Long senderId) {
        log.info("1:1 채팅 메시지 알림 전송 시작 - chatRoomId: {}, messageId: {}, senderId: {}",
                chatRoomId, message.getId(), senderId);

        try {
            User partner = userService.getPartnerUser(chatRoomId, senderId);
            Long partnerId = partner.getId();

            sendMessageToUser(chatRoomId, senderId, message, true);

            if (redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) {
                sendMessageToUser(chatRoomId, partnerId, message, false);

                messageReadService.processAutoReadOnMessageReceive(chatRoomId, partnerId, message.getId());

                log.info("상대방에게 실시간 메시지 전송 및 자동읽음 처리 완료 - partnerId: {}", partnerId);
            } else {
                log.info("상대방이 구독중이 아님 - partnerId: {}, 푸시 알림 등 다른 방식 필요", partnerId);
            }

        } catch (Exception e) {
            log.error("메시지 알림 전송 중 오류 발생 - chatRoomId: {}, messageId: {}",
                    chatRoomId, message.getId(), e);
        }
    }

    public void handleChatListUpdate(Long chatRoomId, Message message, Long senderId) {
        try {
            User partner = userService.getPartnerUser(chatRoomId, senderId);
            Long partnerId = partner.getId();

            publishReceiverChatListUpdate(partnerId, chatRoomId, message);

            log.info("채팅방 목록 양방향 업데이트 완료 - chatRoomId: {}, senderId: {}, partnerId: {}",
                    chatRoomId, senderId, partnerId);

        } catch (Exception e) {
            log.error("채팅방 목록 업데이트 실패 - chatRoomId: {}, senderId: {}",
                    chatRoomId, senderId, e);
        }
    }

    private void sendMessageToUser(Long chatRoomId, Long senderId, Message message, boolean isSentByMe) {
        try {
            WebSocketSession session = chatSessionService.getSession(senderId);

            if (session == null || !session.isOpen()) {
                log.info("사용자 세션이 없거나 닫혀있음 - senderId: {}", senderId);
                return;
            }

            webSocketMessageService.sendMessage(session, message, isSentByMe, chatRoomId, senderId);

            log.info("메시지 전송 성공 - senderId: {}, messageId: {}, isSentByMe: {}", senderId, message.getId(), isSentByMe);
        } catch (Exception e) {
            log.error("사용자에게 메시지 전송 실패 - senderId: {}, messageId: {}",
                    senderId, message.getId(), e);
        }
    }

    private void publishReceiverChatListUpdate(Long receiverId, Long chatRoomId, Message message) {
        int receiverUnreadCount = unreadCountService.getTotalUnreadCount(receiverId);
        publishUnreadBadgeUpdate(receiverId, receiverUnreadCount);

        UserChatUpdateDTO receiverUpdate = UserChatUpdateDTO.builder()
                .chatRoomId(chatRoomId)
                .lastMessageContent(message.getContent())
                .timestamp(message.getCreatedAt())
                .isMyMessage(false)
                .isRead(message.isRead())
                .totalUnreadCount(receiverUnreadCount)
                .build();

        publishUserChatUpdate(receiverId, receiverUpdate);

        log.debug("메시지 수신자 채팅방 목록 업데이트 - receiverId: {}, chatRoomId: {}, unreadCount: {}",
                receiverId, chatRoomId, receiverUnreadCount);
    }

    private void publishUnreadBadgeUpdate(Long userId, int totalUnreadCount) {
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

    private void publishUserChatUpdate(Long userId, UserChatUpdateDTO updateEvent) {
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
