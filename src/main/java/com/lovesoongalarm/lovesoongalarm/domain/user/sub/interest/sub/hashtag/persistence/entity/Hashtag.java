package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.sub.hashtag.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.ELabel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "label")
    private String label;

    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;
}
