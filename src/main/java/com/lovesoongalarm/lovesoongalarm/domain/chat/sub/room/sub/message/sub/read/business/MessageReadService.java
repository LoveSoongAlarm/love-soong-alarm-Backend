package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.implement.MessageReadProcessor;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.implement.MessageReadUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadService {

    private final ChatRoomParticipantService chatRoomParticipantService;

    private final MessageReadProcessor messageReadProcessor;
    private final MessageReadUpdater messageReadUpdater;

    public void processAutoReadOnSubscribe(Long chatRoomId, Long userId) {
        log.info("채팅방 구독 시 자동읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            ChatRoomParticipant participant = chatRoomParticipantService.findByChatRoomIdAndUserId(chatRoomId, userId);

            if (participant == null) {
                log.warn("채팅방 참여자를 찾을 수 없습니다 - chatRoomId: {}, userId: {}", chatRoomId, userId);
                return;
            }

            Long latestMessageId = messageReadProcessor.getLatestMessageId(chatRoomId);

            Long previousLastRead = participant.getLastReadMessageId();
            messageReadUpdater.updateLastReadMessageId(participant, latestMessageId);

            log.info("자동읽음 처리 완료 - chatRoomId: {}, userId: {}, {} -> {}",
                    chatRoomId, userId, previousLastRead, latestMessageId);
        } catch (Exception e) {
            log.error("자동읽음 처리 중 오류 발생 - chatRoomId: {}, userId: {}", chatRoomId, userId, e);
        }
    }
}
