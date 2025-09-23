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
            AND (m.isBlockedMessage = false OR m.user.id = :userId)
            ORDER BY m.id DESC
            LIMIT 1
            """)
    Optional<Message> findLastMessageWithViewerFilter(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId);

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.user
            WHERE m.chatRoom.id = :chatRoomId
            AND (m.isBlockedMessage = false OR m.user.id = :userId)
            ORDER BY m.id DESC
            """)
    List<Message> findRecentMessagesWithViewerFilter(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("""
            SELECT COUNT(m) FROM Message m
            WHERE m.chatRoom.id = :chatRoomId 
            AND m.id < :messageId
            AND (m.isBlockedMessage = false OR m.user.id = :userId)
            """)
    Long countFilteredMessagesBefore(
            @Param("chatRoomId") Long chatRoomId,
            @Param("messageId") Long messageId,
            @Param("userId") Long userId);

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.user
            WHERE m.chatRoom.id = :chatRoomId
            AND (m.isBlockedMessage = false OR m.user.id = :userId)
            AND m.id < :lastMessageId
            ORDER BY m.id DESC
            """)
    List<Message> findPreviousMessagesWithViewerFilter(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    void markAsRead(@Param("messageId") Long messageId);

    @Modifying
    @Query("""
            UPDATE Message m 
            SET m.isRead = true 
            WHERE m.chatRoom.id = :chatRoomId 
            AND m.user.id = :partnerId
            AND m.isRead = false
            """)
    int markUnreadMessagesAsRead(
            @Param("chatRoomId") Long chatRoomId,
            @Param("partnerId") Long partnerId
    );

    @Query("""
            SELECT COUNT(m) FROM Message m
            WHERE m.user.id != :userId 
            AND m.isRead = false
            AND m.isBlockedMessage = false
            AND m.chatRoom.id IN (
                SELECT cp.chatRoom.id 
                FROM ChatRoomParticipant cp 
                WHERE cp.user.id = :userId
            )
            """)
    int countUnreadNonBlockedMessagesForUser(@Param("userId") Long userId);
}
