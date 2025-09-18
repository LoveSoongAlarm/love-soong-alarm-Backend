package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
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
    private String status; // PENDING, COMPLETED, FAILED

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 50)
    private String ipAddress;

    public Pay (String sessionId, String status, String ipAddress) {
        this.sessionId = sessionId;
        this.status = status;
        this.ipAddress = ipAddress;
    }

    public void complete() { this.status = "COMPLETED"; }
    public void fail() { this.status = "FAILED"; }

}
