package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadService {

    private final ChatRoomParticipantService chatRoomParticipantService;
    private final UserService userService;
    private final ReadStatusNotificationService readStatusNotificationService;

    private final MessageRetriever messageRetriever;

    public void processAutoReadOnSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시 자동읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            ChatRoomParticipant participant = chatRoomParticipantService.findByChatRoomIdAndUserId(chatRoomId, userId);

            if (participant == null) {
                log.warn("채팅방 참여자를 찾을 수 없습니다 - chatRoomId: {}, userId: {}", chatRoomId, userId);
                return;
            }

            Long latestMessageId = messageRetriever.getLatestMessageId(chatRoomId);
            if(latestMessageId == null) return;

            chatRoomParticipantService.updateLastReadMessageId(participant, latestMessageId);

            User partner = userService.getPartnerUser(chatRoomId, userId);
            readStatusNotificationService.notifyReadStatusUpdate(chatRoomId, userId, partner.getId(), latestMessageId);

            log.info("자동읽음 처리 완료 - lastReadMessageId: {}", latestMessageId);
        } catch (Exception e) {
            log.error("자동읽음 처리 중 오류 발생 - chatRoomId: {}, userId: {}", chatRoomId, userId, e);
        }
    }
}
