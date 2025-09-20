package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantValidator;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.ChatRoomBlockNotificationService;
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
    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;

    public void blockUserInChatRoom(Long userId, Long chatRoomId) {
        ChatRoomParticipant targetParticipant = chatRoomParticipantRetriever.findByUserIdAndChatRoomId(userId, chatRoomId);
        User target = targetParticipant.getUser();
        chatRoomParticipantValidator.validateBlockRequest(userId, chatRoomId, target.getId());
        chatRoomParticipantUpdater.banUserInChatRoom(target.getId(), chatRoomId);
        chatRoomBlockNotificationService.notifyBlockerSuccess(userId, chatRoomId, target.getId());
    }

    public void unblockUserInChatRoom(Long userId, Long chatRoomId) {
        ChatRoomParticipant targetParticipant = chatRoomParticipantRetriever.findByUserIdAndChatRoomId(userId, chatRoomId);
        User target = targetParticipant.getUser();
        chatRoomParticipantValidator.validateUnblockRequest(userId, chatRoomId);
        chatRoomParticipantUpdater.unbanUserInChatRoom(target.getId(), chatRoomId);
        chatRoomBlockNotificationService.notifyUnblockerSuccess(userId, chatRoomId, target.getId());
    }

    public void validateMessageFromBlockedUser(Long userId, Long chatRoomId) {
        chatRoomParticipantValidator.validateMessageFromBlockedUser(userId, chatRoomId);
    }
}
