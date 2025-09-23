package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import lombok.Builder;

import java.util.List;

@Builder
public record UserMeResponseDTO (
        String name,
        Integer birthDate,
        String major,
        String emoji,
        EGender gender,
        Long userId,
        List<UserMeInterestDTO> interests
) {
    public static UserMeResponseDTO from(User user){
        return UserMeResponseDTO.builder()
                .birthDate(user.getBirthDate())
                .emoji(user.getEmoji())
                .major(user.getMajor())
                .name(user.getNickname())
                .userId(user.getId())
                .gender(user.getGender())
                .interests(user.getInterests().stream().map(
                                interest -> UserMeInterestDTO.from(interest)
                        ).toList()
                ).build();
    }
}