package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

    @Column(nullable = false, unique = true, length = 20)
    private String status; // PENDING, COMPLETED, FAILED

    public Pay (String sessionId, String status) {
        this.sessionId = sessionId;
        this.status = status;
    }

    public void complete() { this.status = "COMPLETED"; }
    public void fail() { this.status = "FAILED"; }
}
