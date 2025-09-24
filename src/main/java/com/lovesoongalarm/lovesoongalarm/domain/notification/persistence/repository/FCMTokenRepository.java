package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.FCMToken;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EDeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    List<FCMToken> findByUserId(Long userId);

    Optional<FCMToken> findByUserIdAndDeviceType(Long userId, EDeviceType deviceType);

    @Modifying
    @Query("DELETE FROM FCMToken f WHERE f.token = :token")
    void deleteByToken(@Param("token") String token);

    @Query("SELECT f.token FROM FCMToken f WHERE f.userId IN :userIds AND f.deviceType = :deviceType")
    List<String> findTokensByUserIdsAndDeviceType(
            @Param("userIds") List<Long> userIds,
            @Param("deviceType") EDeviceType deviceType
    );

    @Query("SELECT f.token FROM FCMToken f WHERE f.userId IN :userIds")
    List<String> findTokensByUserIds(@Param("userIds") List<Long> userIds);

    boolean existsByToken(String token);
}
