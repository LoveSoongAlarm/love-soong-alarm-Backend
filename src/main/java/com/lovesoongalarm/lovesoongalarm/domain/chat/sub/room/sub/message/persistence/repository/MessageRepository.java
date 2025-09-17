package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("""
            SELECT COUNT(m) FROM Message m
            WHERE m.chatRoom.id = :chatRoomId 
            AND m.id < :messageId
            """)
    Long countMessagesByChatRoomIdAndIdLessThan(
            @Param("chatRoomId") Long chatRoomId,
            @Param("messageId") Long messageId);

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.user
            WHERE m.chatRoom.id = :chatRoomId
            AND m.id < :lastMessageId
            ORDER BY m.id DESC
            """)
    List<Message> findPreviousMessagesByChatRoomIdAndLastMessageId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable);

    @Query("""
            SELECT MAX(m.id)
            FROM Message m
            WHERE m.chatRoom.id = :chatRoomId
             """)
    Optional<Long> findLatestMessageIdByChatRoomId(Long chatRoomId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    void markAsRead(@Param("messageId") Long messageId);

    @Modifying
    @Query("""
            UPDATE Message m 
            SET m.isRead = true 
            WHERE m.chatRoom.id = :chatRoomId 
            AND m.user.id != :receiverId 
            AND m.isRead = false
            """)
    int markUnreadMessagesAsRead(
            @Param("chatRoomId") Long chatRoomId,
            @Param("receiverId") Long receiverId
    );

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id IN :messageIds")
    int markSpecificMessagesAsRead(@Param("messageIds") List<Long> messageIds);
}
