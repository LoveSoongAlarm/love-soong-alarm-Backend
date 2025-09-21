package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business.UnreadCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnreadBadgeUpdateService {

    private final UnreadCountService unreadCountService;
    private final WebSocketNotificationSender webSocketNotificationSender;

    public void updateUnreadBadge(Long userId) {
        int totalUnreadCount = unreadCountService.getTotalUnreadCount(userId);
        webSocketNotificationSender.sendUnreadBadgeUpdate(userId, totalUnreadCount);
        log.debug("안 읽은 메시지 배지 업데이트 - userId: {}, count: {}", userId, totalUnreadCount);
    }
}
