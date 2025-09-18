package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.converter.NotificationConverter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.exception.NotificationErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationDeleter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationSaver;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final NotificationDeleter notificationDeleter;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<NotificationResponseDTO> notification(Long userId) {
        try {
            return notificationRetriever.findNoticesByUserId(userId).stream()
                    .map(notificationConverter::toNoticeResponseDTO)
                    .toList();
        } catch (Exception e) {
            log.error("알림 검색 실패. userId = {}", userId, e);
            throw new CustomException(NotificationErrorCode.FIND_NOTIFICATION_ERROR);
        }
    }

    @Transactional
    public Notification saveNotification(Long userId, Long matchingUserId, List<String> interests) {
        User user = userRetriever.findByIdOrElseThrow(userId);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String interestTags = interests.stream()
                .map(EDetailLabel::valueOf)
                .map(EDetailLabel::getValue)
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));

        String message = String.format("내 주변 50m에 %s를 좋아하는 %s이 있어요!", interestTags, user.getGender().getValue());

        LocalDate today = LocalDate.now();

        boolean isNotificationExists = notificationRetriever.existsByUserIdAndMatchingUserIdAndDate(userId, matchingUserId, today);

        if (isNotificationExists) {
            log.debug("중복 알림 : userId={}, matchingUserId={}, now={}", userId, matchingUserId, today);
            return null;
        }

        Notification notification = Notification.create(user, matchingUserId, message, ENotificationStatus.NOT_READ, now, today);
        notificationSaver.save(notification);

        return notification;
    }

    @Transactional
    public void changeStatus(Long userId, Long notificationId) {
        Notification notification = notificationRetriever.findByNotificationIdOrElseThrow(notificationId);

        if (!notification.getUser().getId().equals(userId)) {
            log.error("잘못된 접근입니다. userId={}가 notificationId={}를 변경 시도", userId, notificationId);
            throw new CustomException(NotificationErrorCode.UNAUTHORIZED_NOTIFICATION_ACCESS);
        }

        if (notification.getStatus() == ENotificationStatus.READ) {
            log.debug("이미 읽은 알림입니다. notificationId={}", notificationId);
            return;
        }

        try {
            notification.updateStatus(ENotificationStatus.READ);
        } catch (Exception e) {
            log.error("알림 상태를 변환할 수 없습니다. notificationId={}", notificationId, e);
            throw new CustomException(NotificationErrorCode.CHANGE_NOTIFICATION_STATUS_ERROR);
        }
    }

    @Transactional
    public void changeAllStatus(Long userId) {
        List<Notification> notifications = notificationRetriever.findNoticesByUserId(userId);

        try {
            for (Notification notification : notifications) {
                if (notification.getStatus() != ENotificationStatus.READ) {
                    notification.updateStatus(ENotificationStatus.READ);
                }
            }
        } catch (Exception e) {
            log.error("알림 일괄 읽음 처리 실패. userId={}", userId, e);
            throw new CustomException(NotificationErrorCode.CHANGE_NOTIFICATION_STATUS_ERROR);
        }
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRetriever.findByNotificationIdOrElseThrow(notificationId);

        if (!notification.getUser().getId().equals(userId)) {
            log.error("잘못된 접근입니다. userId={}가 notificationId={}를 삭제 시도", userId, notificationId);
            throw new CustomException(NotificationErrorCode.UNAUTHORIZED_NOTIFICATION_ACCESS);
        }

        try {
            notificationDeleter.delete(notification);
        } catch (Exception e) {
            log.error("알림 삭제 실패. notificationId={}", notificationId, e);
            throw new CustomException(NotificationErrorCode.DELETE_NOTIFICATION_ERROR);
        }
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        List<Notification> notifications = notificationRetriever.findNoticesByUserId(userId);

        try {
            notificationDeleter.deleteAll(notifications);
        } catch (Exception e) {
            log.error("알림 일괄 삭제 실패.", e);
            throw new CustomException(NotificationErrorCode.DELETE_NOTIFICATION_ERROR);
        }
    }
}
