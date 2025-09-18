package com.lovesoongalarm.lovesoongalarm.domain.user.implement;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpdater {

    private final UserRepository userRepository;

    public void decreaseRemainingSlot(Long userId) {
        userRepository.decreaseRemainingSlot(userId);
    }
}
