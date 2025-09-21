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

    public Optional<Message> findLastMessageWithViewerFilter(Long chatRoomId, Long userId) {
        return messageRepository.findLastMessageWithViewerFilter(chatRoomId, userId);
    }

    public List<Message> findRecentMessagesWithViewerFilter(Long chatRoomId, Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return messageRepository.findRecentMessagesWithViewerFilter(chatRoomId, userId, pageable);
    }

    public boolean hasMoreFilteredMessagesBefore(Long chatRoomId, Long oldestMessageId, Long userId) {
        return messageRepository.countFilteredMessagesBefore(chatRoomId, oldestMessageId, userId) > 0;
    }

    public List<Message> findPreviousMessagesWithViewerFilter(Long chatRoomId, Long userId, Long lastMessageId, Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return messageRepository.findPreviousMessagesWithViewerFilter(chatRoomId, userId, lastMessageId, pageable);
    }

    public int countUnreadMessagesForUser(Long userId) {
        return messageRepository.countUnreadNonBlockedMessagesForUser(userId);
    }
}
