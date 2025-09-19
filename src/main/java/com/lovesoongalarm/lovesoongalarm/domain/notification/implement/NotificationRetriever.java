package com.lovesoongalarm.lovesoongalarm.domain.notification.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.notification.exception.NotificationErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository.NotificationRepository;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationRetriever {
    private final NotificationRepository notificationRepository;

    public List<Notification> findNoticesByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByIdDesc(userId);
    }

    public boolean existsByUserIdAndStatus(Long userId) {
        return notificationRepository.existsByUserIdAndStatus(userId, ENotificationStatus.NOT_READ);
    }

    public Notification findByNotificationIdOrElseThrow(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    public boolean existsByUserIdAndMatchingUserIdAndDate(Long userId, Long matchingUserId, LocalDate date) {
        return notificationRepository.existsByUserIdAndMatchingUserIdAndDate(userId, matchingUserId, date);
    }
}
