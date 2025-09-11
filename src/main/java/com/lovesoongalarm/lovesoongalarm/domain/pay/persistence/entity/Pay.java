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
    private String sessionId;

    @Column(nullable = false, unique = true, length = 20)
    private String status;

    public Pay (String sessionId, String status) {
        this.sessionId = sessionId;
        this.status = status;
    }
}
