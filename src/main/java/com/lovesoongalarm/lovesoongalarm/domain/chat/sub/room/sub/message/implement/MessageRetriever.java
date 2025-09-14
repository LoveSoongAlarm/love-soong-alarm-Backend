package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageRetriever {

    private final MessageRepository messageRepository;

    public Optional<Message> findLastMessageByChatRoomId(Long chatRoomId) {
        return messageRepository.findLastMessageByChatRoomId(chatRoomId);
    }

    public List<Message> findRecentMessagesByChatRoomId(Long chatRoomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return messageRepository.findRecentMessagesByChatRoomIdOrderByIdDesc(chatRoomId, pageable);
    }

    public boolean hasMoreMessagesBefore(Long chatRoomId, Long oldestMessageId) {
        return messageRepository.countMessagesByChatRoomIdAndIdLessThan(chatRoomId, oldestMessageId) > 0;
    }
}
