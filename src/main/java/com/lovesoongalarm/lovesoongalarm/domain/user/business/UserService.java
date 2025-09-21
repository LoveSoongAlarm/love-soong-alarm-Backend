package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.implement.RedisPipeline;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserValidator;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRetriever userRetriever;
    private final UserValidator userValidator;
    private final UserUpdater userUpdater;
    private final RedisPipeline redisPipeline;

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
        User user = userRetriever.findByIdAndOnlyActive(userId);
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

    @Transactional
    public void increaseMaxSlot(Long userId) {
        User user = userRetriever.findByIdAndOnlyActive(userId);
      
        if (user.isPrePass()) {
            return;
        }

        int updatedRows = userUpdater.increaseMaxSlot(userId);
        if (updatedRows > 0) {
            log.info("maxSlot 증가 완료 - userId: {}", userId);
        } else {
            log.warn("maxSlot 증가 실패 - 사용자가 존재하지 않거나 이미 처리됨 - userId: {}", userId);
        }
    }

    public void validateChatTicket(User user) {
        userValidator.validateChatTicket(user);
    }

    @Transactional
    public void sweepUserInformation(Long userId) {
        try {
            redisPipeline.pipe(ops -> {
                String stringUserId = String.valueOf(userId);
                String zone = ops.opsForValue().get(ZONE_KEY + stringUserId);

                if (zone != null && !zone.isBlank()) {
                    ops.opsForGeo().remove(GEO_KEY + zone, stringUserId);
                }

                ops.delete(ZONE_KEY + stringUserId);
                ops.delete(LAST_SEEN_KEY + stringUserId);
                ops.delete(USER_GENDER_KEY + stringUserId);
                ops.delete(USER_INTEREST_KEY + stringUserId);
                ops.opsForZSet().remove(LAST_SEEN_INDEX_KEY, stringUserId);
            });
        } catch (Exception e) {
            log.error("redis 유저 정보 삭제 실패 - userId: {}", userId, e);
            throw new CustomException(UserErrorCode.DELETE_USER_ERROR);
        }
    }
}
