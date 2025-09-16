package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.session.business.ChatSessionService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.subscription.implement.RedisSubscriber;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageNotificationService {

    private final RedisSubscriber redisSubscriber;

    private final ChatSessionService chatSessionService;
    private final UserService userService;
    private final MessageReadService messageReadService;
    private final WebSocketMessageService webSocketMessageService;

    public void notifyMessage(Long chatRoomId, Message message, Long senderId) {
        log.info("1:1 채팅 메시지 알림 전송 시작 - chatRoomId: {}, messageId: {}, senderId: {}",
                chatRoomId, message.getId(), senderId);

        try {
            User partner = userService.getPartnerUser(chatRoomId, senderId);
            Long partnerId = partner.getId();

            sendMessageToUser(senderId, message, true);

            if (redisSubscriber.isUserSubscribed(chatRoomId, partnerId)) {
                sendMessageToUser(partnerId, message, false);

                messageReadService.processAutoReadOnMessageReceive(chatRoomId, partnerId, message.getId());

                log.info("상대방에게 실시간 메시지 전송 및 자동읽음 처리 완료 - partnerId: {}", partnerId);
            } else {
                log.info("상대방이 구독중이 아님 - partnerId: {}, 푸시 알림 등 다른 방식 필요", partnerId);
            }

        } catch (Exception e) {
            log.error("메시지 알림 전송 중 오류 발생 - chatRoomId: {}, messageId: {}",
                    chatRoomId, message.getId(), e);
        }
    }

    private void sendMessageToUser(Long userId, Message message, boolean isSentByMe) {
        try {
            WebSocketSession session = chatSessionService.getSession(userId);

            if (session == null || !session.isOpen()) {
                log.info("사용자 세션이 없거나 닫혀있음 - userId: {}", userId);
                return;
            }

            webSocketMessageService.sendMessage(session, message, isSentByMe);

            log.info("메시지 전송 성공 - userId: {}, messageId: {}, isSentByMe: {}", userId, message.getId(), isSentByMe);
        } catch (Exception e) {
            log.error("사용자에게 메시지 전송 실패 - userId: {}, messageId: {}",
                    userId, message.getId(), e);
        }
    }
}
