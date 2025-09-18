package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository.ChatRoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomParticipantUpdater {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    public void updateParticipantStatusToJoined(Long participantId) {
        chatRoomParticipantRepository.updateStatusToJoined(participantId);
    }

    @Transactional
    public void incrementFreeMessageCount(Long participantId) {
        chatRoomParticipantRepository.incrementFreeMessageCount(participantId);
    }
}
