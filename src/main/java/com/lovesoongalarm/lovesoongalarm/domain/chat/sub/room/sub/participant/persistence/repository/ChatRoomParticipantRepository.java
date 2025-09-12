package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    boolean existsByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
