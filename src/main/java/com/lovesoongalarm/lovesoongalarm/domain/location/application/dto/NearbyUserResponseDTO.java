package com.lovesoongalarm.lovesoongalarm.domain.location.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserInterestResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record NearbyUserResponseDTO(
        String name,
        Integer age,
        String major,
        String emoji,
        List<UserInterestResponseDTO> interests,
        Long lastSeen,
        Long matchCount
) {
    public static NearbyUserResponseDTO from(UserResponseDTO base, Long lastSeen, Long matchCount) {
        return NearbyUserResponseDTO.builder()
                .name(base.name())
                .age(base.age())
                .major(base.major())
                .emoji(base.emoji())
                .interests(base.interests())
                .lastSeen(lastSeen)
                .matchCount(matchCount)
                .build();
    }
}