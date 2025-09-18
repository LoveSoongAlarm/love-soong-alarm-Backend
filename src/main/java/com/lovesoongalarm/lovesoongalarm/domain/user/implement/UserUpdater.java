package com.lovesoongalarm.lovesoongalarm.domain.user.implement;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpdater {

    private final UserRepository userRepository;

    @Transactional
    public int decreaseRemainingSlot(Long userId) {
        int updatedRows = userRepository.decreaseRemainingSlot(userId);

        if (updatedRows == 0) {
            log.warn("슬롯 감소 실패 - 사용 가능한 슬롯이 없거나 사용자가 존재하지 않음 - userId: {}", userId);
            return 0;
        }
        return updatedRows;
    }
}
