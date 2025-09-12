package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository.ChatRoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRoomParticipantSaver {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    public List<ChatRoomParticipant> save(List<ChatRoomParticipant> myParticipant) {
        return chatRoomParticipantRepository.saveAll(myParticipant);
    }
}
