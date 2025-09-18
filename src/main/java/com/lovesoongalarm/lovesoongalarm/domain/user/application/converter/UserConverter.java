package com.lovesoongalarm.lovesoongalarm.domain.user.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserSlotResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserSlotResponseDTO createSlotInfo(User user) {
        return UserSlotResponseDTO.builder()
                .maxSlots(user.getMaxSlot())
                .build();
    }
}
