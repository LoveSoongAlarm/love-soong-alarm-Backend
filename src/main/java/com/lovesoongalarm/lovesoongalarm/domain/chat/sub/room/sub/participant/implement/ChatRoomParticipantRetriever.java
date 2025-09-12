package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository.ChatRoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomParticipantRetriever {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    public boolean existsByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return chatRoomParticipantRepository.existsByUserIdAndChatRoomId(userId, chatRoomId);
    }
}
