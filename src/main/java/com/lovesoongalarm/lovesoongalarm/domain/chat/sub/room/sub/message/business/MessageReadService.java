package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.converter.MessageConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReadService {

    private final RedisSubscriber redisSubscriber;
    private final MessageUpdater messageUpdater;

    private final UserService userService;
    private final ChatSessionService chatSessionService;
    private final WebSocketMessageService webSocketMessageService;

    public void processAutoReadOnSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시 자동읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            messageUpdater.markMessagesAsReadByChatRoomAndReceiver(chatRoomId, userId);

            User partner = userService.getPartnerUser(chatRoomId, userId);
            notifyReadStatusUpdate(chatRoomId, userId, partner.getId());

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

        if(!redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) return;

        WebSocketSession partnerSession = chatSessionService.getSession(partnerId);
        if (partnerSession != null && partnerSession.isOpen()) {
            webSocketMessageService.sendReadMessage(partnerSession, chatRoomId);
            log.info("읽음 상태 알림 완료 - partnerId: {}", partnerId);
        } else {
            log.debug("상대방의 세션이 없거나 닫혀있어 읽음 알림을 보내지 않음 - partnerId: {}", partnerId);
        }
    }
}
