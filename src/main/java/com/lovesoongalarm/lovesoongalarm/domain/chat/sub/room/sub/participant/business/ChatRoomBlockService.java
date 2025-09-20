package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantUpdater;
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
    private final ChatRoomParticipantUpdater chatRoomParticipantUpdater;

    public void blockUserInChatRoom(Long userId, Long chatRoomId, Long targetId) {
        chatRoomParticipantValidator.validateBlockRequest(userId, chatRoomId, targetId);
        chatRoomParticipantUpdater.banUserInChatRoom(targetId, chatRoomId);
    }

    public void unblockUserInChatRoom(Long userId, Long chatRoomId, Long targetId) {
        chatRoomParticipantValidator.validateUnblockRequest(userId, chatRoomId);
        chatRoomParticipantUpdater.unbanUserInChatRoom(targetId, chatRoomId);
    }
}
