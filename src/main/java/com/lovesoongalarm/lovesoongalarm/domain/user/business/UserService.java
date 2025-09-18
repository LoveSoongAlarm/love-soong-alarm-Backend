package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserValidator;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRetriever userRetriever;
    private final UserValidator userValidator;
    private final UserUpdater userUpdater;

    public User findUserOrElseThrow(Long userId) {
        return userRetriever.findByIdOrElseThrow(userId);
    }

    public User getPartnerUser(Long roomId, Long userId) {
        log.info("채팅방의 상대방 사용자 정보 조회 시작 - roomId: {}, userId: {}", roomId, userId);
        User partner = userRetriever.findPartnerByChatRoomIdAndUserId(roomId, userId);
        log.info("채팅방의 상대방 사용자 정보 조회 완료 - roomId: {}, partnerId: {}", roomId, partner.getId());
        return partner;
    }

    public void validateChatRoomCreation(Long userId, Long targetUserId) {
        userValidator.validateChatRoomCreation(userId, targetUserId);
    }

    @Transactional
    public void decreaseRemainingSlot(Long userId) {
        User user = userRetriever.findByIdOrElseThrow(userId);
        if (user.isPrePass()) return;

        int updatedRows = userUpdater.decreaseRemainingSlot(userId);
        if (updatedRows == 0) {
            throw new CustomException(UserErrorCode.INSUFFICIENT_CHAT_SLOTS);
        }
    }

    public ChatRoomListDTO.UserSlotInfo createUserSlotInfo(User user) {
        return ChatRoomListDTO.UserSlotInfo.builder()
                .isPrepass(user.isPrePass())
                .maxSlot(user.getMaxSlot())
                .remainingSlot(user.getRemainingSlot())
                .build();
    }
}
