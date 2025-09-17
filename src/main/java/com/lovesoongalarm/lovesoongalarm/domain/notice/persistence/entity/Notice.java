package com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.type.ENoticeStatus;
import com.lovesoongalarm.lovesoongalarm.domain.notice.sub.interest.persistence.entity.NoticeInterest;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice")
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

    public void updateStatus(ENoticeStatus status) {
        this.status = status;
    }
}
