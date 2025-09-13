package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.application.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessageSender messageSender;
    private final MessageRetriever messageRetriever;

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
    }
}
