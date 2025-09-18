package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItem;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItemStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sessionId; // Stripe에서 취급하는 결제 세션 ID

    @Column(nullable = false, length = 20)
    private String status; // PENDING, COMPLETED, FAILED, CANCELED

    @CreatedDate
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String ipAddress;

    public Pay (String sessionId, String status, String ipAddress) {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EItem item; // 예: COIN_1000, COIN_5000

    @Builder
    public Pay (String sessionId, EItemStatus status, User user, EItem item) {
        this.sessionId = sessionId;
        this.status = status;
        this.ipAddress = ipAddress;
        this.user = user;
        this.item = item;
    }

    public static Pay create(String sessionId, EItemStatus status, User user, EItem item){
        return Pay.builder()
                .sessionId(sessionId)
                .status(status)
                .user(user)
                .item(item)
                .build();
    }

    public void complete() { this.status = "COMPLETED"; }
    public void fail() { this.status = "FAILED"; }
    public void cancel() { this.status = "CANCELED"; }

    public void complete() { this.status = EItemStatus.COMPLETED; }
    public void fail() { this.status = EItemStatus.FAILED; }
}
