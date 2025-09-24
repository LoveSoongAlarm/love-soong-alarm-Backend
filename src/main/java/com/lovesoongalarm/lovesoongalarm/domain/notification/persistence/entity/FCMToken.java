package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EDeviceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_tokens",
        indexes = {
                @Index(name = "idx_fcm_user_device", columnList = "user_id, device_type"),
                @Index(name = "idx_fcm_token", columnList = "token")
        })
@EntityListeners(AuditingEntityListener.class)
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EDeviceType deviceType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public FCMToken(Long userId, String token, EDeviceType deviceType) {
        this.userId = userId;
        this.token = token;
        this.deviceType = deviceType;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
