package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;

public interface PayRepository extends JpaRepository<Pay, Long> {
    Optional<Pay> findBySessionId(String sessionId);
}