package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserValidator;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRetriever userRetriever;
    private final UserValidator userValidator;

    public User findUserOrElseThrow(Long userId) {
        return userRetriever.findByIdOrElseThrow(userId);
    }

    public User getPartnerUser(Long roomId, Long userId) {
        log.info("채팅방의 상대방 사용자 정보 조회 시작 - roomId: {}, userId: {}", roomId, userId);
        User partner = userRetriever.findPartnerByChatRoomIdAndUserId(roomId, userId);
        log.info("채팅방의 상대방 사용자 정보 조회 완료 - roomId: {}, partnerId: {}", roomId, partner.getId());
        return partner;
    }

    public void validateTargetUserExists(Long targetUserId) {
        if (!userRetriever.existsById(targetUserId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    public void validateUserSlotAvailability(Long userId) {
        userValidator.validateUserSlotAvailability(userId);
    }
}
