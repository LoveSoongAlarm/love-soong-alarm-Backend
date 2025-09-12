package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
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

    public ChatRoom createChatRoom(Long userId, Long targetUserId) {
        log.info("개인 채팅방 생성 시작 - 본인: {}, 상대방: {}", userId, targetUserId);
        chatRoomValidator.validateChatRoomCreation(userId, targetUserId);

        Optional<ChatRoom> existing = chatRoomRetriever.findByIdAndTargetUserId(userId, targetUserId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 4. 새 채팅방 생성
        log.info("개인 채팅방 생성 완료 -  chatRoomId: {}");
        return null;
    }
}
