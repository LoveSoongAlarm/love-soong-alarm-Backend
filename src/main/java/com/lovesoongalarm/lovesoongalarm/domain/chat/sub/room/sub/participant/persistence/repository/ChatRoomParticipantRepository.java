package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    boolean existsByUser_IdAndChatRoom_Id(Long userId, Long chatRoomId);

    ChatRoomParticipant findByChatRoom_IdAndUser_Id(Long roomId, Long partnerId);

    @Modifying
    @Query("""
            UPDATE ChatRoomParticipant p 
            SET p.lastReadMessageId = :messageId 
            WHERE p.id = :participantId
              AND (p.lastReadMessageId IS NULL OR p.lastReadMessageId < :messageId)
            """)
    void updateLastReadMessageId(Long participantId, Long messageId);
}
