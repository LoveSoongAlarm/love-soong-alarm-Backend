package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnreadCountService {
    
    private final MessageRetriever messageRetriever;

    public int getTotalUnreadCount(Long userId) {
        try {
            int count = messageRetriever.countUnreadMessagesForUser(userId);
            log.info("사용자 전체 안 읽은 메시지 수 조회 - userId: {}, count: {}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("안 읽은 메시지 수 조회 실패 - userId: {}", userId, e);
            return 0;
        }
    }
}
