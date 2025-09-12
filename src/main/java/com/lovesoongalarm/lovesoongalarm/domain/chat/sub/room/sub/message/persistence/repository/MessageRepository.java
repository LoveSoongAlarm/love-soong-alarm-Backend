package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
