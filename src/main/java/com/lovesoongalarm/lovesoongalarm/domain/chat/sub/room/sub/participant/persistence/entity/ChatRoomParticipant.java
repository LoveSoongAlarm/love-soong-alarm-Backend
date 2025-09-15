package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.type.EChatRoomParticipantStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "chat_room_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long lastReadMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EChatRoomParticipantStatus status = EChatRoomParticipantStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoomParticipant(EChatRoomParticipantStatus status, ChatRoom chatRoom, User user) {
        this.lastReadMessageId = 0L;
        this.status = status;
        this.chatRoom = chatRoom;
        this.user = user;
    }

    public static ChatRoomParticipant createJoined(ChatRoom chatRoom, User me) {
        return ChatRoomParticipant.builder()
                .status(EChatRoomParticipantStatus.JOINED)
                .chatRoom(chatRoom)
                .user(me)
                .build();
    }

    public static ChatRoomParticipant createPending(ChatRoom chatRoom, User target) {
        return ChatRoomParticipant.builder()
                .status(EChatRoomParticipantStatus.PENDING)
                .chatRoom(chatRoom)
                .user(target)
                .build();
    }

    public void updateLastReadMessageId(Long messageId) {
        if (messageId != null && (this.lastReadMessageId == null || messageId > this.lastReadMessageId)) {
            this.lastReadMessageId = messageId;
        }
    }
}
