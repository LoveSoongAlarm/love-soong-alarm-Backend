package com.lovesoongalarm.lovesoongalarm.domain.user.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserSlotResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserTicketResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserSlotResponseDTO createSlotInfo(User user) {
        return UserSlotResponseDTO.builder()
                .maxSlots(user.getMaxSlot())
                .build();
    }

    public UserTicketResponseDTO createTicketInfo(User user) {
        return UserTicketResponseDTO.builder()
                .chatTicket(user.getChatTicket())
                .build();
    }
}
