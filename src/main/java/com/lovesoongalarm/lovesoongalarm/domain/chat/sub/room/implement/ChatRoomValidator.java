package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.exception.ChatRoomErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomValidator {

    private final UserRetriever userRetriever;
    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;
    private final ChatRoomRetriever chatRoomRetriever;

    public void validateChatRoomCreation(Long userId, Long targetUserId) {
        validateNotSelfChat(userId, targetUserId);
        validateTargetUserExists(targetUserId);
    }

    public void validateChatRoomAccess(Long userId, Long roomId) {
        validateChatRoomExists(roomId);
        validateChatRoomAuthorization(userId, roomId);
    }

    private void validateNotSelfChat(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new CustomException(ChatRoomErrorCode.CANNOT_CHAT_WITH_SELF);
        }
    }

    private void validateTargetUserExists(Long targetUserId) {
        if (!userRetriever.existsById(targetUserId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateChatRoomExists(Long roomId) {
        if (!chatRoomRetriever.existsById(roomId)) {
            throw new CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    private void validateChatRoomAuthorization(Long userId, Long roomId) {
        if (!chatRoomParticipantRetriever.existsByUserIdAndChatRoomId(userId, roomId)) {
            throw new CustomException(ChatRoomErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
    }
}
