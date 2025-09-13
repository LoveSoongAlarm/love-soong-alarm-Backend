package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseDTO(
        String name,
        Integer age,
        String major,
        String emoji,
        List<UserInterestResponseDTO> interests
) {
    public static UserResponseDTO from(User user, Integer age){
        return UserResponseDTO.builder()
                .age(age)
                .emoji(user.getEmoji())
                .major(user.getMajor())
                .name(user.getNickname())
                .interests(user.getInterests().stream().map(
                        interest -> UserInterestResponseDTO.from(interest)
                ).toList()
                ).build();
    }
}
