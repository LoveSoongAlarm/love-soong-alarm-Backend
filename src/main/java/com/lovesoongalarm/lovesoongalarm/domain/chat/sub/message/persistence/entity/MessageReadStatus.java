package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.message.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="message_read_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long chatRoomId;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private boolean isRead;
}
