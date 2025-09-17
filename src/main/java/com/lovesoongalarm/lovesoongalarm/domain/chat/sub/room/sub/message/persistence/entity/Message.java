package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Message(String content, ChatRoom chatRoom, User user, boolean isRead) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.user = user;
        this.isRead = isRead;
    }

    public static Message create(String content, ChatRoom chatRoom, User user) {
        return Message.builder()
                .content(content)
                .chatRoom(chatRoom)
                .user(user)
                .isRead(false)
                .build();
    }

    public boolean isSentBy(Long userId) {
        return this.user.getId().equals(userId);
    }
}
