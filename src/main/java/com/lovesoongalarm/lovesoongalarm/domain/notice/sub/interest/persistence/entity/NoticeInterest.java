package com.lovesoongalarm.lovesoongalarm.domain.notice.sub.interest.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notice_interest")
public class NoticeInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Column(name = "interest")
    @Enumerated(EnumType.STRING)
    private EDetailLabel interest;
}
