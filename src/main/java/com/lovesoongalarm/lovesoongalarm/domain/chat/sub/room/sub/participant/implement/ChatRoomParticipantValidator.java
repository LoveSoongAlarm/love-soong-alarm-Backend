package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.exception.ChatRoomParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomParticipantValidator {

    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;

    public void validateBlockRequest(Long userId, Long chatRoomId, Long targetId) {
        validateSelfBlock(userId, targetId);
        validateActionPermission(userId, chatRoomId);
        validateTargetInChatRoom(chatRoomId, targetId);
        validateDuplicateBlock(chatRoomId, targetId);
    }

    public void validateUnblockRequest(Long userId, Long chatRoomId) {
        validateActionPermission(userId, chatRoomId);
    }

    private void validateSelfBlock(Long userId, Long targetId) {
        if (userId.equals(targetId)) {
            throw new CustomException(ChatRoomParticipantErrorCode.CANNOT_BLOCK_YOURSELF);
        }
    }

    private void validateActionPermission(Long userId, Long chatRoomId) {
        if (!chatRoomParticipantRetriever.existsByUserIdAndChatRoomId(userId, chatRoomId)) {
            throw new CustomException(ChatRoomParticipantErrorCode.USER_NOT_PARTICIPANT_IN_CHAT_ROOM);
        }
    }

    private void validateTargetInChatRoom(Long chatRoomId, Long targetId) {
        if (!chatRoomParticipantRetriever.existsByUserIdAndChatRoomId(targetId, chatRoomId)) {
            throw new CustomException(ChatRoomParticipantErrorCode.TARGET_USER_NOT_IN_CHAT_ROOM);
        }
    }

    private void validateDuplicateBlock(Long chatRoomId, Long targetId) {
        if (chatRoomParticipantRetriever.isUserBannedInChatRoom(targetId, chatRoomId)) {
            throw new CustomException(ChatRoomParticipantErrorCode.USER_ALREADY_BLOCKED);
        }
    }
}
