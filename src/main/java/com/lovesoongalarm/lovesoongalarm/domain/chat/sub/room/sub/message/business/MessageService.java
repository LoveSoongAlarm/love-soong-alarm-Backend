package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.converter.MessageConverter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.MessageListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event.MessageSentEvent;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.application.dto.ChatTicketValidationResult;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatRoomParticipantService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business.ChatTicketService;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRetriever messageRetriever;
    private final MessageValidator messageValidator;
    private final MessageSaver messageSaver;
    private final MessageSender messageSender;

    private final MessageConverter messageConverter;

    private final UserService userService;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatTicketService chatTicketService;

    private final ApplicationEventPublisher eventPublisher;

    private static final int INITIAL_MESSAGE_LIMIT = 50;
    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;

    public ChatRoomListDTO.LastMessageInfo createLastMessageInfo(ChatRoom chatRoom, Long userId) {
        log.info("마지막 메시지 정보 생성 시작 - chatRoomId: {}, userId: {}", chatRoom.getId(), userId);

        Optional<Message> lastMessage = messageRetriever.findLastMessageByChatRoomId(chatRoom.getId());

        if (lastMessage.isEmpty()) {
            log.info("마지막 메시지가 없음 - chatRoomId: {}", chatRoom.getId());
            return ChatRoomListDTO.LastMessageInfo.empty();
        }

        Message message = lastMessage.get();
        boolean isSentByMe = message.getUser().getId().equals(userId);

        if (isSentByMe) {
            log.info("내가 보낸 마지막 메시지 - messageId: {}, isRead: {}", message.getId(), message.isRead());

            return ChatRoomListDTO.LastMessageInfo.sentByMe(
                    message.getContent(),
                    message.getCreatedAt(),
                    message.isRead()
            );
        } else {
            log.info("상대방이 보낸 마지막 메시지 - messageId: {}, isRead: {}", message.getId(), message.isRead());

            return ChatRoomListDTO.LastMessageInfo.sentByPartner(
                    message.getContent(),
                    message.getCreatedAt(),
                    message.isRead()
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

    public MessageListDTO.Response getPreviousMessages(
            Long chatRoomId, Long userId, Long lastMessageId, Integer size) {
        log.info("과거 메시지 조회 시작 - chatRoomId: {}, userId: {}, lastMessageId: {}, size: {}",
                chatRoomId, userId, lastMessageId, size);

        int pageSize = validateAndGetPageSize(size);
        List<Message> messages = messageRetriever.findPreviousMessages(chatRoomId, lastMessageId, pageSize);

        Long nextCursor = null;
        boolean hasMoreMessages = false;

        if (!messages.isEmpty()) {
            Long oldestMessageId = messages.get(messages.size() - 1).getId();
            hasMoreMessages = messageRetriever.hasMoreMessagesBefore(chatRoomId, oldestMessageId);
            nextCursor = hasMoreMessages ? oldestMessageId : null;
        }

        List<Message> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();

        List<MessageListDTO.MessageInfo> messageInfos = sortedMessages.stream()
                .map(message -> messageConverter.toMessageInfo(message, userId))
                .toList();

        log.info("과거 메시지 조회 완료 - chatRoomId: {}, 조회된 메시지 수: {}, hasMore: {}",
                chatRoomId, messageInfos.size(), hasMoreMessages);

        return messageConverter.toMessageListResponse(messageInfos, hasMoreMessages, nextCursor);
    }

    @Transactional
    public void sendMessage(WebSocketSession session, ChatRoom chatRoom, String content, Long senderId) {
        log.info("1:1 채팅 메시지 전송 시작 - chatRoomId: {}, senderId: {}", chatRoom.getId(), senderId);
        ChatTicketValidationResult validation = chatTicketService.validateMessageSending(senderId, chatRoom.getId());
        if (!validation.canSend()) {
            messageSender.sendMessageCountLimitWithTicketInfo(session, validation);
            return;
        }
        messageValidator.validateMessage(content);

        User sender = userService.findUserOrElseThrow(senderId);

        Message message = Message.create(content, chatRoom, sender);
        Message savedMessage = messageSaver.save(message);

        chatRoomParticipantService.activatePartnerIfPending(chatRoom, savedMessage, senderId);

        eventPublisher.publishEvent(
                MessageSentEvent.builder()
                        .chatRoomId(chatRoom.getId())
                        .message(savedMessage)
                        .senderId(senderId)
                        .build()
        );

        log.info("1:1 채팅 메시지 전송 완료 - messageId: {}", savedMessage.getId());
    }

    private int validateAndGetPageSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
