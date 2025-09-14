package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.user 
            WHERE m.chatRoom.id = :chatRoomId
            ORDER BY m.id DESC
            LIMIT 1
            """)
    Optional<Message> findLastMessageByChatRoomId(Long chatRoomId);

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.user
            WHERE m.chatRoom.id = :chatRoomId
            ORDER BY m.id DESC
            """)
    List<Message> findRecentMessagesByChatRoomIdOrderByIdDesc(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable);
}
