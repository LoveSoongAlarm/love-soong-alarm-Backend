package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
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

    public ChatRoomListDTO.ChatRoomInfo toChatRoomInfo(ChatRoom chatRoom, User partner, ChatRoomListDTO.LastMessageInfo lastMessageInfo) {
        return ChatRoomListDTO.ChatRoomInfo.builder()
                .chatRoomId(chatRoom.getId())
                .emoji(partner.getEmoji())
                .partnerNickname(partner.getNickname())
                .lastMessageInfo(lastMessageInfo)
                .build();
    }
}
