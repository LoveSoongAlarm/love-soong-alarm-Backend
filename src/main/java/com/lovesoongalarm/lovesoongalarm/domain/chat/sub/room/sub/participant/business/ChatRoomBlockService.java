package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatRoomBlockService {

    private final ChatRoomParticipantValidator chatRoomParticipantValidator;

    public void blockUserInChatRoom(Long userId, Long chatRoomId, Long targetId) {
        chatRoomParticipantValidator.validateBlockRequest(userId, chatRoomId, targetId);
    }

    public void unblockUserInChatRoom(Long userId, Long chatRoomId) {
        chatRoomParticipantValidator.validateUnblockRequest(userId, chatRoomId);
    }
}
