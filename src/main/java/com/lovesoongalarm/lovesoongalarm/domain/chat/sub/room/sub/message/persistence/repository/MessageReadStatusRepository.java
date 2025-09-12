package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
}
