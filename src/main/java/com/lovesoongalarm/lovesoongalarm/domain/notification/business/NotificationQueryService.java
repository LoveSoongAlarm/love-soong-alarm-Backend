package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.converter.NotificationConverter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationSaver;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {
    private final UserRetriever userRetriever;
    private final NotificationRetriever notificationRetriever;
    private final NotificationConverter notificationConverter;
    private final NotificationSaver notificationSaver;

    @Transactional
    public List<NotificationResponseDTO> notification(Long userId) {
        return notificationRetriever.findNoticesByUserId(userId).stream()
                .map(notificationConverter::toNoticeResponseDTO)
                .toList();
    }

    @Transactional
    public void sendNotification(Long userId, Long matchingUserId, List<String> interests) {
        User user = userRetriever.findById(userId);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String interestTags = interests.stream()
                .map(EDetailLabel::valueOf)
                .map(EDetailLabel::getValue)
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));

        String message = String.format("내 주변 50m에 %s를 좋아하는 %s이 있어요!", interestTags, user.getGender().getValue());

        LocalDate today = LocalDate.now();

        boolean isNotificationExists = notificationRetriever.existsByUserIdAndMatchingUserIdAndDate(userId, matchingUserId, today);

        if(isNotificationExists) {
            log.info("중복 알림 : userId={}, matchingUserId={}, now={}", userId, matchingUserId, today);
            return;
        }

        Notification notification = Notification.create(user, matchingUserId, message, ENotificationStatus.NOT_READ, now, today);
        notificationSaver.save(notification);
    }
}
