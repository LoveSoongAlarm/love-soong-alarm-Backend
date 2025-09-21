package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByIdDesc(Long userId);

    boolean existsByUserIdAndStatus(Long userId, ENotificationStatus status);

    Optional<Notification> findById(Long id);

    boolean existsByUserIdAndMatchingUserIdAndDate(Long userId, Long matchingUserId, LocalDate date);
}
