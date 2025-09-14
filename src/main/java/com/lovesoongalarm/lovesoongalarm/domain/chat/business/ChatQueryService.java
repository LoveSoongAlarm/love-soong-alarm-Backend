package com.lovesoongalarm.lovesoongalarm.domain.chat.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter.ChatRoomConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomDetailDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.ChatMessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatRoomService chatRoomService;
    private final ChatRoomConverter chatRoomConverter;

    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatMessageService chatMessageService;

    public ChatRoomListDTO.Response getChatRoomList(Long userId) {
        log.info("채팅방 목록 조회 시작 - userId = {}", userId);
        List<ChatRoom> chatRoomList = chatRoomService.getUserChatRooms(userId);
        List<ChatRoomListDTO.ChatRoomInfo> chatRoomInfos = chatRoomList.stream()
                        .map(chatRoom -> chatRoomService.createChatRoomInfo(chatRoom, userId))
                        .toList();
        log.info("채팅방 목록 조회 종료 - userId = {}", userId);
        return chatRoomConverter.toChatRoomListResponse(chatRoomInfos);
    }

    public ChatRoomDetailDTO.Response getChatRoomDetail(Long userId, Long roomId) {
        log.info("초기 채팅방 조회 시작 - userId = {}, roomId = {}", userId, roomId);
        ChatRoom chatRoom = chatRoomService.getChatRoomWithValidation(userId, roomId);
        User partner = chatRoomParticipantService.getPartnerUser(chatRoom, userId);
        List<Message> recentMessages = chatMessageService.getRecentMessages(roomId);
        Boolean hasMoreMessages = chatMessageService.hasMoreMessages(roomId, recentMessages);
        log.info("채팅방 상세 조회 완료 - chatRoomId: {}, partnerId: {}, messageCount: {}, hasMore: {}",
                roomId, partner.getId(), recentMessages.size(), hasMoreMessages);
        return null;
    }
}
