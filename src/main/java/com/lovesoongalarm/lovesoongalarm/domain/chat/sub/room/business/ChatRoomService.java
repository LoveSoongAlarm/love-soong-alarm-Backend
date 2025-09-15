package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter.ChatRoomConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.MessageService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.business.SubscriptionService;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomValidator chatRoomValidator;
    private final ChatRoomRetriever chatRoomRetriever;
    private final ChatRoomSaver chatRoomSaver;

    private final MessageService messageService;
    private final SubscriptionService subscriptionService;

    private final ChatRoomConverter chatRoomConverter;

    public ChatRoom createChatRoom(Long userId, Long targetUserId) {
        log.info("개인 채팅방 생성 시작 - 본인: {}, 상대방: {}", userId, targetUserId);
        chatRoomValidator.validateChatRoomCreation(userId, targetUserId);

        Optional<ChatRoom> existing = chatRoomRetriever.findByIdAndTargetUserId(userId, targetUserId);
        if (existing.isPresent()) {
            log.info("이미 참여중인 채팅방이므로 채팅방 그대로 반환 - chatRoomId: {}", existing.get().getId());
            return existing.get();
        }

        ChatRoom newRoom = ChatRoom.create();
        ChatRoom savedRoom = chatRoomSaver.save(newRoom);
        log.info("개인 채팅방 생성 완료 -  chatRoomId: {}", savedRoom.getId());
        return savedRoom;
    }

    public List<ChatRoom> getUserChatRooms(Long userId) {
        return chatRoomRetriever.findChatRoomsByUserIdOrderByLastMessageIdDesc(userId);
    }

    public ChatRoomListDTO.ChatRoomInfo createChatRoomInfo(ChatRoom chatRoom, Long userId) {
        ChatRoomParticipant myParticipant = null;
        ChatRoomParticipant partnerParticipant = null;

        for (ChatRoomParticipant participant : chatRoom.getParticipants()) {
            if (participant.getUser().getId().equals(userId)) {
                myParticipant = participant;
            } else {
                partnerParticipant = participant;
            }
        }

        if (myParticipant == null || partnerParticipant == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        ChatRoomListDTO.LastMessageInfo lastMessageInfo = messageService.createLastMessageInfo(
                chatRoom, userId, myParticipant, partnerParticipant);

        return chatRoomConverter.toChatRoomInfo(chatRoom, partnerParticipant.getUser(), lastMessageInfo);
    }

    public void validateChatRoomAccess(Long userId, Long roomId) {
        log.info("채팅방 조회 및 권한 검증 시작 - userId: {}, roomId: {}", userId, roomId);
        chatRoomValidator.validateChatRoomAccess(userId, roomId);
        log.info("채팅방 조회 및 권한 검증 완료 - roomId: {}", roomId);
    }

    public void subscribeToChatRoom(WebSocketSession session, Long chatRoomId, Long userId) {
        chatRoomValidator.validateChatRoomAccess(userId, chatRoomId);
        subscriptionService.subscribeToChatRoom(session, chatRoomId, userId);
    }
}
