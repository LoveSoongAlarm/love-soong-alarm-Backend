package com.lovesoongalarm.lovesoongalarm.security.info;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, ERole role) {
    public static JwtUserInfo of(Long userId, ERole role){
        return JwtUserInfo.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
