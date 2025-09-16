package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.exception.ChatRoomErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomRetriever {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<ChatRoom> findByIdAndTargetUserId(Long userId, Long targetUserId) {
        return chatRoomRepository.findByIdAndTargetUserId(userId, targetUserId);
    }

    public List<ChatRoom> findChatRoomsByUserIdOrderByLastMessageIdDesc(Long userId) {
        return chatRoomRepository.findChatRoomsByUserIdOrderByLastMessageIdDesc(userId);
    }

    public boolean existsById(Long roomId) {
        return chatRoomRepository.existsById(roomId);
    }

    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));
    }
}
