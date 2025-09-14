package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    boolean existsByUser_IdAndChatRoom_Id(Long userId, Long chatRoomId);

    ChatRoomParticipant findByChatRoom_IdAndUser_Id(Long roomId, Long partnerId);
}
