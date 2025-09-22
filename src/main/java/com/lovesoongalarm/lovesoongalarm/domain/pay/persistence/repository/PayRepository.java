package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.repository;

import java.util.Optional;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;

public interface PayRepository extends JpaRepository<Pay, Long> {
    @Query("select p from Pay p where p.sessionId = :sessionId")
    Optional<Pay> findBySessionId(String sessionId);

    Optional<Pay> findBySessionIdAndUser(String sessionId, User user);
}