package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageRetriever {

    private final MessageRepository messageRepository;

    public Optional<Message> findLastMessageByChatRoomId(Long chatRoomId) {
        return messageRepository.findLastMessageByChatRoomId(chatRoomId);
    }
}
