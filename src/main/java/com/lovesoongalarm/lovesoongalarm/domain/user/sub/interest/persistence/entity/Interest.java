package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.ELabel;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.sub.hashtag.persistence.entity.Hashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "detatil_label")
    @Enumerated(EnumType.STRING)
    private EDetailLabel detailLabel;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Hashtag> hashtags = new ArrayList<>();

    @Builder
    public Interest(Long id,ELabel label, User user, EDetailLabel detailLabel) {
        this.id = id;
        this.label = label;
        this.user = user;
        this.detailLabel = detailLabel;
    }

    public static Interest create(ELabel label, User user, EDetailLabel detailLabel) {
        return Interest.builder()
                .label(label)
                .user(user)
                .detailLabel(detailLabel)
                .build();
    }

    public void addHashtags(List<Hashtag> hashtags) {
        this.hashtags.addAll(hashtags);
    }

    public void updateInterestFromProfile(ELabel label, EDetailLabel detailLabel) {
        this.detailLabel= detailLabel;
        this.label = label;
    }
}
