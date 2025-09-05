package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "birth_date")
    private Integer birthDate;

    @Column(name = "height")
    private Integer height;

    @Column(name = "major", nullable = false)
    private String major;
}
