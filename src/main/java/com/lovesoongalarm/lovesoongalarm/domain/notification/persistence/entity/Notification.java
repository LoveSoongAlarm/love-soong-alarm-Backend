package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.ENotificationStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification", uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_user_matching",
                columnNames = {"user_id", "matching_user_id", "date"}
        )
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "matching_user_id")
    private Long matchingUserId;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ENotificationStatus status;

    @Column(name = "notification_time")
    private String notificationTime;

    @Column(name = "date")
    private LocalDate date;

    @Builder
    public Notification(User user, Long matchingUserId, String message, ENotificationStatus status, String notificationTime, LocalDate date) {
        this.user = user;
        this.matchingUserId = matchingUserId;
        this.message = message;
        this.status = status;
        this.notificationTime = notificationTime;
        this.date = date;
    }

    public static Notification create(User user, Long matchingUserId, String message, ENotificationStatus status, String notificationTime, LocalDate date) {
        return Notification.builder()
                .user(user)
                .matchingUserId(matchingUserId)
                .message(message)
                .status(status)
                .notificationTime(notificationTime)
                .date(date)
                .build();
    }

    public void updateStatus(ENotificationStatus status) {
        this.status = status;
    }
}
