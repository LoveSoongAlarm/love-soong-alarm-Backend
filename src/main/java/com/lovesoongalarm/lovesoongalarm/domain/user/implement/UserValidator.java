package com.lovesoongalarm.lovesoongalarm.domain.user.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {

    private final UserRetriever userRetriever;

    public void validateChatRoomCreation(Long userId, Long targetUserId) {
        validateTargetUserExists(targetUserId);
        validateUserSlotAvailability(userId);
    }

    private void validateTargetUserExists(Long targetUserId) {
        if (!userRetriever.existsById(targetUserId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateUserSlotAvailability(Long userId) {
        User user = userRetriever.findByIdOrElseThrow(userId);
        if (user.isPrePass()) return;
        if(!user.hasAvailableSlot()){
            throw new CustomException(UserErrorCode.INSUFFICIENT_CHAT_SLOTS);
        }
    }
}
