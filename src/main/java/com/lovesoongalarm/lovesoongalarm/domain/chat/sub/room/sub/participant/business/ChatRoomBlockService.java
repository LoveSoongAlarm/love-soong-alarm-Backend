package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantValidator;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.ChatRoomBlockNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.MessageSender;
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
    private final ChatRoomBlockNotificationService chatRoomBlockNotificationService;

    public void blockUserInChatRoom(Long userId, Long chatRoomId, Long targetId) {
        chatRoomParticipantValidator.validateBlockRequest(userId, chatRoomId, targetId);
        chatRoomParticipantUpdater.banUserInChatRoom(targetId, chatRoomId);
        chatRoomBlockNotificationService.notifyBlockerSuccess(userId, chatRoomId, targetId);
    }

    public void unblockUserInChatRoom(Long userId, Long chatRoomId, Long targetId) {
        chatRoomParticipantValidator.validateUnblockRequest(userId, chatRoomId);
        chatRoomParticipantUpdater.unbanUserInChatRoom(targetId, chatRoomId);
        chatRoomBlockNotificationService.notifyUnblockerSuccess(userId, chatRoomId, targetId);
    }

    public void validateMessageFromBlockedUser(Long userId, Long chatRoomId) {
        chatRoomParticipantValidator.validateMessageFromBlockedUser(userId, chatRoomId);
    }
}
