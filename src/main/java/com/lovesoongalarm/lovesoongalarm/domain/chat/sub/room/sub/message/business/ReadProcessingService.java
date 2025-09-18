package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging.WebSocketNotificationSender;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.subscription.business.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadProcessingService {

    private final MessageRetriever messageRetriever;
    private final UnreadCountService unreadCountService;
    private final UserSubscriptionService userSubscriptionService;
    private final WebSocketNotificationSender webSocketNotificationSender;

    public void handleSubscribeReadResult(MessageReadService.ReadResult readResult) {
        if (!readResult.hasUpdate()) {
            return;
        }

        log.info("구독 시 읽음 후처리 시작 - readCount: {}, partnerId: {}",
                readResult.readCount(), readResult.partnerId());

        try {
            webSocketNotificationSender.sendReadNotification(
                    readResult.chatRoomId(),
                    readResult.readerId(),
                    readResult.partnerId()
            );

            updateMyUnreadBadge(readResult.readerId());

            handleChatListUpdateOnRead(
                    readResult.chatRoomId(),
                    readResult.readerId(),
                    readResult.partnerId(),
                    readResult.readCount()
            );
            log.info("구독 시 읽음 후처리 완료 - partnerId: {}", readResult.partnerId());
        } catch (Exception e) {
            log.error("구독 시 읽음 후처리 중 오류", e);
        }
    }

    public void handleMessageReceiveReadResult(MessageReadService.ReadResult readResult) {
        if (!readResult.hasUpdate()) {
            return;
        }

        log.info("메시지 수신 시 읽음 후처리 시작 - partnerId: {}", readResult.partnerId());

        try {
            webSocketNotificationSender.sendReadNotification(
                    readResult.chatRoomId(),
                    readResult.readerId(),
                    readResult.partnerId()
            );

        } catch (Exception e) {
            log.error("메시지 수신 시 읽음 후처리 중 오류", e);
        }
    }

    private void handleChatListUpdateOnRead(Long chatRoomId, Long userId, Long partnerId, int readCount) {
        try {
            Optional<Message> lastMessage = messageRetriever.findLastMessageByChatRoomId(chatRoomId);
            if (lastMessage.isEmpty()) {
                log.debug("마지막 메시지가 없어서 채팅방 목록 업데이트를 건너뜀 - chatRoomId: {}", chatRoomId);
                return;
            }

            Message message = lastMessage.get();
            notifyPartnerOfReadStatus(chatRoomId, partnerId, message);

            int updatedUnreadCount = unreadCountService.getTotalUnreadCount(userId);
            userSubscriptionService.publishUnreadBadgeUpdate(userId, updatedUnreadCount);

            log.info("읽음 처리 시 채팅방 목록 업데이트 완료 - userId: {}, chatRoomId: {}, readCount: {}, newUnreadCount: {}",
                    userId, chatRoomId, readCount, updatedUnreadCount);

        } catch (Exception e) {
            log.error("읽음 처리 시 채팅방 목록 업데이트 실패 - userId: {}, chatRoomId: {}", userId, chatRoomId, e);
        }
    }

    private void notifyPartnerOfReadStatus(Long chatRoomId, Long partnerId, Message lastMessage) {
        try {
            boolean isPartnerMessage = lastMessage.getUser().getId().equals(partnerId);

            if (isPartnerMessage) {
                UserChatUpdateDTO partnerUpdate = UserChatUpdateDTO.builder()
                        .chatRoomId(chatRoomId)
                        .lastMessageContent(lastMessage.getContent())
                        .timestamp(lastMessage.getCreatedAt())
                        .isMyMessage(true)
                        .isRead(lastMessage.isRead())
                        .build();

                userSubscriptionService.publishUserChatUpdate(partnerId, partnerUpdate);
                log.debug("상대방에게 읽음 상태 업데이트 알림 전송 - partnerId: {}, chatRoomId: {}", partnerId, chatRoomId);
            }

        } catch (Exception e) {
            log.error("상대방 읽음 상태 알림 실패 - partnerId: {}, chatRoomId: {}", partnerId, chatRoomId, e);
        }
    }

    private void updateMyUnreadBadge(Long userId) {
        try {
            int myUnreadCount = unreadCountService.getTotalUnreadCount(userId);
            userSubscriptionService.publishUnreadBadgeUpdate(userId, myUnreadCount);
            log.info("구독자 배지 업데이트 완료 - userId: {}, unreadCount: {}", userId, myUnreadCount);
        } catch (Exception e) {
            log.error("구독자 배지 업데이트 실패 - userId: {}", userId, e);
        }
    }
}
