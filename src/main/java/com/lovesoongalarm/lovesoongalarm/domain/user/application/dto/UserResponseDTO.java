package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseDTO(
        Long id,
        String name,
        Integer age,
        String major,
        String emoji,
        String lastSeen,
        List<UserInterestResponseDTO> interests
) {
    public static UserResponseDTO from(User user, Integer age, String lastSeen){
        return UserResponseDTO.builder()
                .id(user.getId())
                .age(age)
                .emoji(user.getEmoji())
                .major(user.getMajor())
                .name(user.getNickname())
                .interests(user.getInterests().stream().map(
                        interest -> UserInterestResponseDTO.from(interest)
                ).toList()
                )
                .lastSeen(lastSeen)
                .build();
    }
}
