package com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.type.ENoticeStatus;
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
@Table(name = "notice", uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_user_matching",
                columnNames = {"user_id", "matching_user_id", "date"}
        )
})
public class Notice {
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
    private ENoticeStatus status;

    @Column(name = "notice_time")
    private String noticeTime;

    @Column(name = "date")
    private LocalDate date;

    @Builder
    public Notice(User user, Long matchingUserId, String message, ENoticeStatus status, String noticeTime, LocalDate date) {
        this.user = user;
        this.matchingUserId = matchingUserId;
        this.message = message;
        this.status = status;
        this.noticeTime = noticeTime;
        this.date = date;
    }

    public static Notice create(User user, Long matchingUserId, String message, ENoticeStatus status, String noticeTime, LocalDate date) {
        return Notice.builder()
                .user(user)
                .matchingUserId(matchingUserId)
                .message(message)
                .status(status)
                .noticeTime(noticeTime)
                .date(date)
                .build();
    }

    public void updateStatus(ENoticeStatus status) {
        this.status = status;
    }
}
