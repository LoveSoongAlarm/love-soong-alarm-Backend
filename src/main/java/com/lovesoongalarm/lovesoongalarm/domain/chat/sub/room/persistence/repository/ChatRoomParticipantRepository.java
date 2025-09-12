package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
}
