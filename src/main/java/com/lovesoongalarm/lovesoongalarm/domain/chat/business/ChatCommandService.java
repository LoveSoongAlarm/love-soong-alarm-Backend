package com.lovesoongalarm.lovesoongalarm.domain.chat.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter.ChatRoomConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final ChatRoomService chatRoomService;
    private final ChatRoomParticipantService chatRoomParticipantService;

    private final ChatRoomConverter chatRoomConverter;

    public ChatRoomCreateDTO.Response createChatRoom(Long userId, ChatRoomCreateDTO.Request request) {
        log.info("채팅방 생성 및 본인 참여 시작 - userId: {}, targetUserId: {}", userId, request.targetUserId());
        ChatRoom chatRoom = chatRoomService.createChatRoom(userId, request.targetUserId());
        chatRoomParticipantService.addParticipant(userId, chatRoom.getId());
        log.info("채팅방 생성 및 본인 참여 시작 - userId: {}, chatRoomId: {}", userId, chatRoom.getId());
        return chatRoomConverter.toCreateChatRoomResponse(chatRoom.getId());
    }
}
