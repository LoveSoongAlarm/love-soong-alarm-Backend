package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN cr.participants cp
            WHERE cp.user.id IN (:userId, :targetUserId)
            GROUP BY cr.id
            HAVING COUNT(DISTINCT cp.user.id) = 2
            """)
    Optional<ChatRoom> findByIdAndTargetUserId(
            @Param("userId") Long userId,
            @Param("targetUserId") Long targetUserId);


    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.participants cp
            JOIN FETCH cp.user
            WHERE cr.id IN (
                SELECT DISTINCT myParticipant.chatRoom.id\s
                FROM ChatRoomParticipant myParticipant\s
                WHERE myParticipant.user.id = :userId\s
                AND myParticipant.status = 'JOINED'
            )
            ORDER BY COALESCE(
                (SELECT MAX(m.id)
                FROM Message m
                WHERE m.chatRoom.id = cr.id), 0) DESC
            """)
    List<ChatRoom> findChatRoomsByUserIdOrderByLastMessageIdDesc(@Param("userId") Long userId);
}
