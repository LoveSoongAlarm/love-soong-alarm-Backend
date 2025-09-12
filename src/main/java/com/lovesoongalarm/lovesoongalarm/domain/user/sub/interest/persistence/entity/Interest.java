package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.ELabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.sub.hashtag.persistence.entity.Hashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "interests")
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "label")
    @Enumerated(EnumType.STRING)
    private ELabel label;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "detatil_label")
    @Enumerated(EnumType.STRING)
    private EDetailLabel detailLabel;
}
