package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.implement.UserRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomValidator chatRoomValidator;
    private final ChatRoomRetriever chatRoomRetriever;
    private final ChatRoomSaver chatRoomSaver;

    public ChatRoom createChatRoom(Long userId, Long targetUserId) {
        log.info("개인 채팅방 생성 시작 - 본인: {}, 상대방: {}", userId, targetUserId);
        chatRoomValidator.validateChatRoomCreation(userId, targetUserId);

        Optional<ChatRoom> existing = chatRoomRetriever.findByIdAndTargetUserId(userId, targetUserId);
        if (existing.isPresent()) {
            return existing.get();
        }

        ChatRoom newRoom = ChatRoom.create();
        ChatRoom savedRoom = chatRoomSaver.save(newRoom);
        log.info("개인 채팅방 생성 완료 -  chatRoomId: {}", savedRoom.getId());
        return savedRoom;
    }
}
