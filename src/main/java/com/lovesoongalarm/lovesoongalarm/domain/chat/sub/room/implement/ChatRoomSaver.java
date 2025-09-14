package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomSaver {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom save(ChatRoom chatRoom){
        return chatRoomRepository.save(chatRoom);
    }
}
