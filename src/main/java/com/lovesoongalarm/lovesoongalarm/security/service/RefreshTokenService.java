package com.lovesoongalarm.lovesoongalarm.security.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.lovesoongalarm.lovesoongalarm.common.constant.Constants.REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    @Getter
    private Integer refreshExpiration;

    private final StringRedisTemplate stringRedisTemplate;

    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshExpiration,
                TimeUnit.MILLISECONDS
        );
    }

    public void updateRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        String existingToken = stringRedisTemplate.opsForValue().get(key);

        if (existingToken != null) {
            deleteRefreshToken(userId);
        }
        saveRefreshToken(userId, refreshToken);
    }

    public String getRefreshToken(Long userId){
        String key = REFRESH_TOKEN_PREFIX + userId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(Long userId){
        String key = REFRESH_TOKEN_PREFIX + userId;
        stringRedisTemplate.delete(key);
    }

    public boolean validateRefreshToken(Long userId, String refreshToken){
        String storedToken = getRefreshToken(userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }

}
