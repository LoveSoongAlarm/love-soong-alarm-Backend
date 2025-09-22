package com.lovesoongalarm.lovesoongalarm.domain.chat.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter.ChatRoomConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomDetailDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business.ChatRoomService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.MessageListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
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
    private final UserService userService;
    private final MessageService messageService;
    private final ChatRoomParticipantService chatRoomParticipantService;

    private final ChatRoomConverter chatRoomConverter;

    public ChatRoomListDTO.Response getChatRoomList(Long userId) {
        log.info("채팅방 목록 조회 시작 - userId = {}", userId);
        User user = userService.findUserOrElseThrow(userId);
        ChatRoomListDTO.UserSlotInfo userSlotInfo = userService.createUserSlotInfo(user);
        List<ChatRoom> chatRoomList = chatRoomService.getUserChatRooms(userId);
        List<ChatRoomListDTO.ChatRoomInfo> chatRoomInfos = chatRoomList.stream()
                .map(chatRoom -> chatRoomService.createChatRoomInfo(chatRoom, userId))
                .toList();
        log.info("채팅방 목록 조회 종료 - userId = {}", userId);
        return chatRoomConverter.toChatRoomListResponse(userSlotInfo, chatRoomInfos);
    }

    public ChatRoomDetailDTO.Response getChatRoomDetail(Long userId, Long chatRoomId) {
        log.info("초기 채팅방 조회 시작 - userId = {}, chatRoomId = {}", userId, chatRoomId);
        chatRoomService.validateChatRoomAccess(userId, chatRoomId);
        User partner = userService.getPartnerUser(chatRoomId, userId);

        boolean isPartnerBlocked = chatRoomParticipantService.getPartnerBlockStatus(chatRoomId, partner.getId());

        List<Message> recentMessages = messageService.getRecentMessages(chatRoomId, userId);
        boolean hasMoreMessages = messageService.hasMoreMessages(chatRoomId, recentMessages, userId);

        log.info("채팅방 상세 조회 완료 - chatRoomId: {}, partnerId: {}, messageCount: {}, hasMore: {}",
                chatRoomId, partner.getId(), recentMessages.size(), hasMoreMessages);
        return chatRoomConverter.toChatRoomDetailResponse(
                partner, recentMessages, userId, hasMoreMessages, isPartnerBlocked);
    }

    public MessageListDTO.Response getChatRoomMessages(Long userId, Long chatRoomId, Integer size, Long lastMessageId) {
        log.info("채팅방 과거 메시지 조회 시작 - userId: {}, chatRoomId: {}, lastMessageId: {}, size: {}",
                userId, chatRoomId, lastMessageId, size);
        chatRoomService.validateChatRoomAccess(userId, chatRoomId);
        MessageListDTO.Response response = messageService.getPreviousMessages(
                chatRoomId, userId, lastMessageId, size);
        log.info("채팅방 과거 메시지 조회 완료 - userId: {}, chatRoomId: {}, lastMessageId: {}, size: {}",
                userId, chatRoomId, lastMessageId, size);
        return response;
    }
}
