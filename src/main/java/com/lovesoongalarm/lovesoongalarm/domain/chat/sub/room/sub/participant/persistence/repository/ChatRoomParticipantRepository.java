package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    boolean existsByUser_IdAndChatRoom_Id(Long userId, Long chatRoomId);

    @Modifying
    @Query("""
            UPDATE ChatRoomParticipant p 
            SET p.status = 'JOINED' 
            WHERE p.id = :participantId
            """)
    void updateStatusToJoined(@Param("participantId") Long participantId);

    @Query("""
            SELECT crp FROM ChatRoomParticipant crp
            JOIN FETCH crp.user u
            WHERE crp.user.id = :userId
            AND crp.chatRoom.id = :chatRoomId
            """)
    Optional<ChatRoomParticipant> findByUser_IdAndChatRoom_Id(
            @Param("userId") Long userId,
            @Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("""
            UPDATE ChatRoomParticipant p 
            SET p.freeMessageCount = p.freeMessageCount + 1 
            WHERE p.id = :participantId 
            AND p.ticketUsed = false
            """)
    void incrementFreeMessageCount(@Param("participantId") Long participantId);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
            FROM ChatRoomParticipant p 
            WHERE p.user.id = :userId 
            AND p.chatRoom.id = :chatRoomId 
            AND p.status = 'BANNED'
            """)
    boolean isUserBannedInChatRoom(
            @Param("userId") Long userId,
            @Param("chatRoomId") Long chatRoomId);
}
