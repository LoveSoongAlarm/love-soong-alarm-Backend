package com.lovesoongalarm.lovesoongalarm.domain.notification.implement;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationRetriever {
    private final NotificationRepository notificationRepository;

    public List<Notification> findNoticesByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public boolean existsByUserIdAndMatchingUserIdAndDate(Long userId, Long matchingUserId, LocalDate date) {
        return notificationRepository.existsByUserIdAndMatchingUserIdAndDate(userId, matchingUserId, date);
    }
}
