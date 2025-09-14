package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.converter.MessageConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.MessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSender;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageSender messageSender;
    private final MessageRetriever messageRetriever;

    private final MessageConverter messageConverter;

    private static final int INITIAL_MESSAGE_LIMIT = 50;

    @Transactional
    public void sendConnectionSuccessMessage(Long userId, String userNickname, WebSocketSession session) {
        WebSocketMessageDTO.ConnectionInfo connectionInfo = WebSocketMessageDTO.ConnectionInfo.builder()
                .type(EWebSocketMessageType.CONNECTION_SUCCESS)
                .userId(userId)
                .userNickname(userNickname)
                .timestamp(LocalDateTime.now())
                .message("WebSocket 연결이 성공했습니다.")
                .build();

        messageSender.sendMessage(session, connectionInfo);
    }

    public ChatRoomListDTO.LastMessageInfo createLastMessageInfo(
            ChatRoom chatRoom, Long userId, ChatRoomParticipant myParticipant, ChatRoomParticipant partnerParticipant) {
        log.info("마지막 메시지 정보 생성 시작 - chatRoomId: {}, userId: {}", chatRoom.getId(), userId);

        Optional<Message> lastMessage = messageRetriever.findLastMessageByChatRoomId(chatRoom.getId());

        if (lastMessage.isEmpty()) {
            log.info("마지막 메시지가 없음 - chatRoomId: {}", chatRoom.getId());
            return ChatRoomListDTO.LastMessageInfo.empty();
        }

        Message message = lastMessage.get();
        boolean isSentByMe = message.getUser().getId().equals(userId);

        if (isSentByMe) {
            boolean isReadByPartner = isMessageRead(message.getId(), partnerParticipant.getLastReadMessageId());
            log.info("내가 보낸 마지막 메시지 - messageId: {}, partnerLastReadMessageId: {}, isRead: {}",
                    message.getId(), partnerParticipant.getLastReadMessageId(), isReadByPartner);

            return ChatRoomListDTO.LastMessageInfo.sentByMe(
                    message.getContent(),
                    message.getCreatedAt(),
                    isReadByPartner
            );
        } else {
            boolean isReadByMe = isMessageRead(message.getId(), myParticipant.getLastReadMessageId());
            log.info("상대방이 보낸 마지막 메시지 - messageId: {}, myLastReadMessageId: {}, isRead: {}",
                    message.getId(), myParticipant.getLastReadMessageId(), isReadByMe);

            return ChatRoomListDTO.LastMessageInfo.sentByPartner(
                    message.getContent(),
                    message.getCreatedAt(),
                    isReadByMe
            );
        }
    }

    public List<Message> getRecentMessages(Long roomId) {
        log.info("채팅방 최근 메시지 조회 시작 - chatRoomId: {}", roomId);
        List<Message> messages = messageRetriever.findRecentMessagesByChatRoomId(roomId, INITIAL_MESSAGE_LIMIT);
        log.info("채팅방 최근 메시지 조회 완료 - chatRoomId: {}, messageCount: {}", roomId, messages.size());
        return messages;
    }

    public boolean hasMoreMessages(Long chatRoomId, List<Message> messages) {
        if (messages.isEmpty()) {
            return false;
        }

        Long oldestMessageId = messages.get(messages.size() - 1).getId();
        return messageRetriever.hasMoreMessagesBefore(chatRoomId, oldestMessageId);
    }

    public MessageDTO.ListResponse getPreviousMessages(
            Long chatRoomId, Long userId, Long lastMessageId, Integer pageSize, Long partnerLastReadMessageId) {
        log.info("과거 메시지 조회 시작 - chatRoomId: {}, userId: {}, lastMessageId: {}, size: {}",
                chatRoomId, userId, lastMessageId, pageSize);

        List<Message> messages = messageRetriever.findPreviousMessages(chatRoomId, lastMessageId, pageSize);

        Long nextCursor = null;
        boolean hasMoreMessages = false;

        if (!messages.isEmpty()) {
            Long oldestMessageId = messages.get(messages.size() - 1).getId();
            hasMoreMessages = messageRetriever.hasMoreMessagesBefore(chatRoomId, oldestMessageId);
            nextCursor = hasMoreMessages ? oldestMessageId : null;
        }

        List<MessageDTO.MessageInfo> messageInfos = messages.stream()
                .map(message -> messageConverter.toMessageInfo(message, userId, partnerLastReadMessageId))
                .toList();
    }

    private boolean isMessageRead(Long messageId, Long lastReadMessageId) {
        if (lastReadMessageId == null) {
            return false;
        }

        return messageId <= lastReadMessageId;
    }
}
