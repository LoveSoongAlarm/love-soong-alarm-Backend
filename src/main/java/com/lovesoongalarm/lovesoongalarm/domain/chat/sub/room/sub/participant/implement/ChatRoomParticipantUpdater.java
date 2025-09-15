package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository.ChatRoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomParticipantUpdater {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    public void updateLastReadMessageId(Long participantId, Long latestMessageId) {
        chatRoomParticipantRepository.updateLastReadMessageId(participantId, latestMessageId);
    }
}
