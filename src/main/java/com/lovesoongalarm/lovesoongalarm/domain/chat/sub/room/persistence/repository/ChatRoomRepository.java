package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
