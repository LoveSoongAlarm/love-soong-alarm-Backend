package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatRoomConverter {

    public ChatRoomCreateDTO.Response toCreateChatRoomResponse(Long chatRoomId) {
        return ChatRoomCreateDTO.Response.builder()
                .chatRoomId(chatRoomId)
                .build();
    }

    public ChatRoomListDTO.Response toChatRoomListResponse(List<ChatRoomListDTO.ChatRoomInfo> chatRoomInfos) {
        return new ChatRoomListDTO.Response(chatRoomInfos);
    }
}
