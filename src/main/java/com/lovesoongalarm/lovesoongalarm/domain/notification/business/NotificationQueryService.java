package com.lovesoongalarm.lovesoongalarm.domain.notification.business;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.converter.NotificationConverter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationBadgeUpdateEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationStatusAllChangeEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationStatusChangeEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.exception.NotificationErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationDeleter;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notification.implement.NotificationSaver;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity.Notification;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationType;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(readOnly = true)
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

        String message = String.format("내 주변 50m에 %s를 좋아하는 %s이 있어요!", interestTags, getOppositeGenderValue(user.getGender()));

        LocalDate today = LocalDate.now();

        if(notificationRetriever.existsByUserIdAndMatchingUserIdAndDate(userId, matchingUserId, today)) {
            return null;
        }

        Notification notification = Notification.create(user, matchingUserId, message, ENotificationStatus.NOT_READ, now, today);


        try {
            notificationSaver.save(notification);
            return notification;
        } catch (UnexpectedRollbackException e) {
            log.warn("알림 저장 트랜잭션 롤백됨, 무시: userId={}, matchId={}", userId, matchingUserId);
            return null;
        } catch (DataIntegrityViolationException e) {
            log.debug("중복 알림 무시: userId={}, matchingUserId={}", userId, matchingUserId);
            return null;
        } catch (Exception e) {
            log.error("알림 저장 실패: userId={}, matchingUserId={}", userId, matchingUserId, e);
            return null;
        }
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

            boolean hasUnread = notificationRetriever.existsByUserIdAndStatus(userId);

            applicationEventPublisher.publishEvent(
                    NotificationStatusChangeEvent.builder()
                            .type(ENotificationType.READ)
                            .userId(userId)
                            .notificationId(notificationId)
                            .build()
            );

            if (!hasUnread) {
                applicationEventPublisher.publishEvent(
                        NotificationBadgeUpdateEvent.builder()
                                .userId(userId)
                                .hasUnRead(false)
                                .build()
                );
            }
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

            applicationEventPublisher.publishEvent(
                    NotificationStatusAllChangeEvent.builder()
                            .type(ENotificationType.READ)
                            .userId(userId)
                            .isAll(true)
                            .build()
            );

            applicationEventPublisher.publishEvent(
                    NotificationBadgeUpdateEvent.builder()
                            .userId(userId)
                            .hasUnRead(false)
                            .build()
            );
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

            applicationEventPublisher.publishEvent(
                    NotificationStatusChangeEvent.builder()
                            .type(ENotificationType.DELETE)
                            .userId(userId)
                            .notificationId(notificationId)
                            .build()
            );

            boolean hasUnread = notificationRetriever.existsByUserIdAndStatus(userId);
            if (!hasUnread) {
                applicationEventPublisher.publishEvent(
                        NotificationBadgeUpdateEvent.builder()
                                .userId(userId)
                                .hasUnRead(false)
                                .build()
                );
            }
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

            applicationEventPublisher.publishEvent(
                    NotificationStatusAllChangeEvent.builder()
                            .type(ENotificationType.DELETE)
                            .userId(userId)
                            .isAll(true)
                            .build()
            );

            applicationEventPublisher.publishEvent(
                    NotificationBadgeUpdateEvent.builder()
                            .userId(userId)
                            .hasUnRead(false)
                            .build()
            );
        } catch (Exception e) {
            log.error("알림 일괄 삭제 실패.", e);
            throw new CustomException(NotificationErrorCode.DELETE_NOTIFICATION_ERROR);
        }
    }

    private String getOppositeGenderValue(EGender gender) {
        if (gender == EGender.MALE) {
            return EGender.FEMALE.getValue();
        } else if (gender == EGender.FEMALE) {
            return EGender.MALE.getValue();
        } else {
            return gender.getValue();
        }
    }
}
