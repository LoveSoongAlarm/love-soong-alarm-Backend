package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.converter.MessageConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSaver;
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
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReadService {

    private final MessageRetriever messageRetriever;
    private final RedisSubscriber redisSubscriber;

    private final UserService userService;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatSessionService chatSessionService;
    private final WebSocketMessageService webSocketMessageService;

    public void processAutoReadOnSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시 자동읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            ChatRoomParticipant participant = chatRoomParticipantService.findByChatRoomIdAndUserId(chatRoomId, userId);

            if (participant == null) {
                log.warn("채팅방 참여자를 찾을 수 없습니다 - chatRoomId: {}, userId: {}", chatRoomId, userId);
                return;
            }

            Long latestMessageId = messageRetriever.getLatestMessageId(chatRoomId);
            if(latestMessageId == null){
                log.info("채팅방에 메시지가 없으므로 자동읽음 처리 조기 종료 - latestMessageId: {}", latestMessageId);
                return;
            }

            chatRoomParticipantService.updateLastReadMessageId(participant, latestMessageId);

            User partner = userService.getPartnerUser(chatRoomId, userId);
            notifyReadStatusUpdate(chatRoomId, userId, partner.getId(), latestMessageId);

            log.info("자동읽음 처리 완료 - lastReadMessageId: {}", latestMessageId);
        } catch (Exception e) {
            log.error("자동읽음 처리 중 오류 발생 - chatRoomId: {}, userId: {}", chatRoomId, userId, e);
        }
    }

    private void notifyReadStatusUpdate(Long chatRoomId, Long readerId, Long partnerId, Long lastReadMessageId) {
        log.info("읽음 상태 알림 - chatRoomId: {}, readerId: {}, partnerId: {}, lastReadMessageId: {}",
                chatRoomId, readerId, partnerId, lastReadMessageId);

        if(!redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) return;

        WebSocketSession partnerSession = chatSessionService.getSession(partnerId);
        if (partnerSession != null && partnerSession.isOpen()) {
            webSocketMessageService.sendReadMessage(partnerSession, chatRoomId, lastReadMessageId);
            log.info("읽음 상태 알림 완료 - lastReadMessageId: {}", lastReadMessageId);
        } else {
            log.debug("상대방의 세션이 없거나 닫혀있어 읽음 알림을 보내지 않음 - partnerId: {}", partnerId);
        }
    }
}
