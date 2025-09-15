package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.sub.read.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageReadProcessor {

    private final MessageRepository messageRepository;

    public Long getLatestMessageId(Long chatRoomId) {
        return messageRepository.findLatestMessageIdByChatRoomId(chatRoomId)
                .orElse(null);
    }
}
