package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageUpdater {

    private final MessageRepository messageRepository;

    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.markAsRead(messageId);
        log.info("메시지 읽음 처리 완료 - messageId: {}", messageId);
    }

    @Transactional
    public void markMessagesAsReadByChatRoomAndReceiver(Long chatRoomId, Long receiverId) {
        int updatedCount = messageRepository.markUnreadMessagesAsRead(chatRoomId, receiverId);
        log.info("채팅방 미읽은 메시지 일괄 읽음 처리 완료 - chatRoomId: {}, receiverId: {}, updatedCount: {}",
                chatRoomId, receiverId, updatedCount);
    }

    @Transactional
    public void markSpecificMessagesAsRead(List<Long> messageIds) {
        if (messageIds.isEmpty()) return;

        int updatedCount = messageRepository.markSpecificMessagesAsRead(messageIds);
        log.info("특정 메시지들 읽음 처리 완료 - messageIds: {}, updatedCount: {}", messageIds, updatedCount);
    }
}
