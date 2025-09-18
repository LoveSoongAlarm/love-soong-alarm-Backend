package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EGender;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "birth_date")
    private Integer birthDate;

    @Column(name = "major")
    private String major;

    @Column(name = "serial_id", nullable = false)
    private String serialId;

    @Column(name = "platform", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private EPlatform platform;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EUserStatus status;

    @Column(name = "emoji")
    private String emoji;

    @Column(name = "chat_ticket")
    @NotNull @Min(0)
    private Integer chatTicket = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    @Column(name = "pre_pass")
    private boolean prePass = false;

    @Column(name = "max_slot", nullable = false)
    @NotNull @Min(1)
    private Integer maxSlot = 1;

    @Column(name = "remaining_slot", nullable = false)
    @NotNull @Min(0)
    private Integer remainingSlot = 1;

    @Builder
    private User(EPlatform platform, ERole role, String serialId, EUserStatus status, Integer chatTicket, Integer maxSlot, Integer remainingSlot, boolean prePass) {
        this.platform = platform;
        this.role = role;
        this.serialId = serialId;
        this.status = status;
        this.chatTicket = chatTicket;
        this.maxSlot = maxSlot;
        this.remainingSlot = remainingSlot;
        this.prePass = prePass;
    }

    public static User create(String serialId, EPlatform platform, ERole role, EUserStatus status) {
        return User.builder()
                .platform(platform)
                .serialId(serialId)
                .role(role)
                .status(status)
                .chatTicket(0)
                .maxSlot(1)
                .remainingSlot(1)
                .prePass(false)
                .build();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateMajor(String major) {
        this.major = major;
    }

    public void updateBirthDate(Integer birthDate) {
        this.birthDate = birthDate;
    }

    public void updateGender(EGender gender) {
        this.gender = gender;
    }

    public void updateEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void updateFromOnboardingAndProfile(String nickname, String major, Integer birthDate, EGender gender, String emoji) {
        this.nickname = nickname;
        this.major = major;
        this.birthDate = birthDate;
        this.gender = gender;
        this.emoji = emoji;
        this.status = EUserStatus.ACTIVE;
    }

    public boolean hasAvailableSlot() {
        if( this.remainingSlot != null && this.remainingSlot > 0 ) {
            return true;
        }
        return false;
    }

    public void decreaseChatTicket() {
        this.chatTicket--;
    }
}
