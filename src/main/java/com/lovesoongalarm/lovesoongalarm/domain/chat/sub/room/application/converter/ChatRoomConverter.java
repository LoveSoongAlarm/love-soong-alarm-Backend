package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomConverter {

    public ChatRoomCreateDTO.Response toCreateChatRoomResponse(Long chatRoomId) {
        return ChatRoomCreateDTO.Response.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
