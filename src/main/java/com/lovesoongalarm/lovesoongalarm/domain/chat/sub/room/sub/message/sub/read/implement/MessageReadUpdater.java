package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageReadUpdater {

    private final ChatRoomParticipantService chatRoomParticipantService;

    public void updateLastReadMessageId(ChatRoomParticipant participant, Long messageId) {
        if (participant.getLastReadMessageId() == null ||
                messageId > participant.getLastReadMessageId()) {

            participant.updateLastReadMessageId(messageId);
            chatRoomParticipantService.save(participant);

            log.info("lastReadMessageId 업데이트 완료 - participantId: {}, messageId: {}",
                    participant.getId(), messageId);
        } else {
            log.debug("더 이전 메시지이므로 업데이트 하지 않음 - participantId: {}, currentLastRead: {}, requestMessageId: {}",
                    participant.getId(), participant.getLastReadMessageId(), messageId);
        }
    }
}
