package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.type.EChatRoomParticipantStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "chat_room_participants",
        indexes = {
                @Index(name = "idx_chat_room_participants_room_user",
                        columnList = "chat_room_id, user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EChatRoomParticipantStatus status = EChatRoomParticipantStatus.PENDING;

    @Column(name = "free_message_count", nullable = false)
    private Integer freeMessageCount = 0;

    @Column(name = "ticket_used", nullable = false)
    private Boolean ticketUsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoomParticipant(EChatRoomParticipantStatus status, ChatRoom chatRoom, User user) {
        this.status = status;
        this.chatRoom = chatRoom;
        this.user = user;
        this.freeMessageCount = 0;
        this.ticketUsed = false;
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

    public boolean hasUnlimitedChat() {
        return this.getUser().isPrePass() || this.getTicketUsed();
    }

    public void setTicketUsed() {
        this.ticketUsed = true;
    }

    public int getTicketCount() {
        return this.getUser().getChatTicket();
    }

    public void banUser() {
        this.status = EChatRoomParticipantStatus.BANNED;
    }

    public void unbanUser() {
        if (this.status == EChatRoomParticipantStatus.BANNED) {
            this.status = EChatRoomParticipantStatus.JOINED;
        }
    }

    public boolean canSendMessage() {
        return this.status.canSendMessage();
    }

    public boolean isBanned() {
        return this.status.isBanned();
    }
}
