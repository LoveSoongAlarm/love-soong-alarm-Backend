package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event.listener;

import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.ChatMessageNotificationService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.event.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEventListener {

    private final ChatMessageNotificationService chatMessageNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSent(MessageSentEvent event) {
        log.info("트랜잭션 커밋 후 웹소켓 메시지 전송 시작 - chatRoomId: {}, messageId: {}",
                event.chatRoomId(), event.message().getId());

        try {
            chatMessageNotificationService.notifyNewMessage(
                    event.chatRoomId(),
                    event.message(),
                    event.senderId()
            );
            log.info("웹소켓 메시지 전송 완료 - messageId: {}", event.message().getId());
        } catch (Exception e) {
            log.error("웹소켓 메시지 전송 실패 - chatRoomId: {}, messageId: {}",
                    event.chatRoomId(), event.message().getId(), e);
        }
    }
}
