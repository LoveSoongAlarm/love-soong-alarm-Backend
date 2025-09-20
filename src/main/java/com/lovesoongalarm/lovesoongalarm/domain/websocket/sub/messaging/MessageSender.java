package com.lovesoongalarm.lovesoongalarm.domain.websocket.sub.messaging;

import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationWebSocketDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type.EWebSocketNotificationType;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.dto.WebSocketMessageDTO;
import com.lovesoongalarm.lovesoongalarm.domain.websocket.persistence.type.EWebSocketMessageType;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.UserChatUpdateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageTransmitter;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSender {

    private final MessageTransmitter messageTransmitter;

    public void sendConnectionSuccessMessage(Long userId, String userNickname, WebSocketSession session) {
        WebSocketMessageDTO.ConnectionInfo connectionInfo = WebSocketMessageDTO.ConnectionInfo.builder()
                .type(EWebSocketMessageType.CONNECTION_SUCCESS)
                .userId(userId)
                .userNickname(userNickname)
                .timestamp(LocalDateTime.now())
                .message("WebSocket 연결이 성공했습니다.")
                .build();

        messageTransmitter.sendMessage(session, connectionInfo);
    }

    public void sendErrorMessage(WebSocketSession session, String errorCode, String message) {
        if (!session.isOpen()) {
            log.warn("세션이 닫혀있어 에러 메시지를 전송할 수 없습니다.");
            return;
        }

        WebSocketMessageDTO.ErrorResponse errorResponse = WebSocketMessageDTO.ErrorResponse.builder()
                .type(EWebSocketMessageType.ERROR)
                .errorCode(errorCode)
                .message(message)
                .build();

        messageTransmitter.sendMessage(session, errorResponse);
    }

    public void sendSubscribeSuccessMessage(WebSocketSession session, Long chatRoomId) {
        WebSocketMessageDTO.SubscribeSuccess subscribeSuccess = WebSocketMessageDTO.SubscribeSuccess.builder()
                .type(EWebSocketMessageType.SUBSCRIBE)
                .chatRoomId(chatRoomId)
                .message("채팅방 구독에 성공했습니다.")
                .build();

        messageTransmitter.sendMessage(session, subscribeSuccess);
    }

    public void sendUnsubscribeSuccessMessage(WebSocketSession session, Long chatRoomId) {
        WebSocketMessageDTO.SubscribeSuccess unsubscribeSuccess = WebSocketMessageDTO.SubscribeSuccess.builder()
                .type(EWebSocketMessageType.UNSUBSCRIBE)
                .chatRoomId(chatRoomId)
                .message("채팅방 구독 해제에 성공했습니다.")
                .build();

        messageTransmitter.sendMessage(session, unsubscribeSuccess);
    }

    public void sendChatListSubscribeSuccessMessage(WebSocketSession session) {
        WebSocketMessageDTO.ChatListSubscribeSuccess subscribeSuccess = WebSocketMessageDTO.ChatListSubscribeSuccess.builder()
                .type(EWebSocketMessageType.CHAT_LIST_SUBSCRIBE)
                .message("채팅방 목록 구독에 성공했습니다.")
                .build();

        messageTransmitter.sendMessage(session, subscribeSuccess);
    }

    public void sendChatListUnsubscribeSuccessMessage(WebSocketSession session) {
        WebSocketMessageDTO.ChatListSubscribeSuccess unsubscribeSuccess = WebSocketMessageDTO.ChatListSubscribeSuccess.builder()
                .type(EWebSocketMessageType.CHAT_LIST_UNSUBSCRIBE)
                .message("채팅방 목록 구독 해제에 성공했습니다.")
                .build();

        messageTransmitter.sendMessage(session, unsubscribeSuccess);
    }

    public void sendNewChatRoomNotification(WebSocketSession session, Long chatRoomId, String partnerNickname, String partnerEmoji) {
        WebSocketMessageDTO.NewChatRoomNotification notification = WebSocketMessageDTO.NewChatRoomNotification.builder()
                .type(EWebSocketMessageType.NEW_CHAT_ROOM_CREATED)
                .chatRoomId(chatRoomId)
                .partnerNickname(partnerNickname)
                .partnerEmoji(partnerEmoji)
                .createdAt(LocalDateTime.now())
                .build();

        messageTransmitter.sendMessage(session, notification);
    }

    public void sendReadMessage(WebSocketSession session, Long chatRoomId, Long readerId) {
        WebSocketMessageDTO.MessageReadNotification messageReadNotification = WebSocketMessageDTO.MessageReadNotification.builder()
                .type(EWebSocketMessageType.MESSAGE_READ)
                .chatRoomId(chatRoomId)
                .readerId(readerId)
                .build();

        messageTransmitter.sendMessage(session, messageReadNotification);
    }

    public void sendMessage(WebSocketSession session, Message message, boolean isSentByMe, Long chatRoomId, Long senderId) {
        WebSocketMessageDTO.ChatMessage chatMessage = WebSocketMessageDTO.ChatMessage.builder()
                .type(EWebSocketMessageType.CHAT_MESSAGE)
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .messageId(message.getId())
                .content(message.getContent())
                .timestamp(message.getCreatedAt())
                .isSentByMe(isSentByMe)
                .build();

        messageTransmitter.sendMessage(session, chatMessage);
    }

    public void sendUnreadBadgeUpdate(WebSocketSession session, int totalUnreadCount) {
        WebSocketMessageDTO.UnreadBadgeUpdate unreadBadgeUpdate = WebSocketMessageDTO.UnreadBadgeUpdate.builder()
                .type(EWebSocketMessageType.UNREAD_BADGE_UPDATE)
                .totalUnreadCount(totalUnreadCount)
                .build();

        messageTransmitter.sendMessage(session, unreadBadgeUpdate);
    }

    public void sendChatListUpdate(WebSocketSession session, UserChatUpdateDTO updateEvent) {
        WebSocketMessageDTO.ChatListUpdate chatListUpdate = WebSocketMessageDTO.ChatListUpdate.builder()
                .type(EWebSocketMessageType.CHAT_LIST_UPDATE)
                .chatRoomId(updateEvent.chatRoomId())
                .lastMessageContent(updateEvent.lastMessageContent())
                .timestamp(updateEvent.timestamp())
                .isMyMessage(updateEvent.isMyMessage())
                .isRead(updateEvent.isRead())
                .build();

        messageTransmitter.sendMessage(session, chatListUpdate);
    }

    public void sendMessageCountLimit(WebSocketSession session){
        WebSocketMessageDTO.MessageCountLimit messageCountLimit = WebSocketMessageDTO.MessageCountLimit.builder()
                .type(EWebSocketMessageType.MESSAGE_COUNT_LIMIT)
                .build();

        messageTransmitter.sendMessage(session, messageCountLimit);
    }

    public void sendNotification(WebSocketSession session, NotificationWebSocketDTO.Notification notification) {
        messageTransmitter.sendMessage(session, notification);
    }

    public void sendUnreadBadgeUpdate(WebSocketSession session, boolean hasUnread) {
        NotificationWebSocketDTO.UnreadNotificationBadge unreadNotificationBadge = NotificationWebSocketDTO.UnreadNotificationBadge.builder()
                .type(EWebSocketNotificationType.UNREAD_NOTIFICATION_BADGE_UPDATE)
                .hasUnread(hasUnread)
                .build();

        messageTransmitter.sendMessage(session, unreadNotificationBadge);
    }

    public void sendReadNotification(WebSocketSession session, Long notificationId) {
        NotificationWebSocketDTO.ChangeNotification readNotification = NotificationWebSocketDTO.ChangeNotification.builder()
                .type(EWebSocketNotificationType.READ_NOTIFICATION)
                .notificationId(notificationId)
                .build();

        messageTransmitter.sendMessage(session, readNotification);
    }

    public void sendAllReadNotification(WebSocketSession session, boolean isAll) {
        NotificationWebSocketDTO.AllChangeNotification allReadNotification = NotificationWebSocketDTO.AllChangeNotification.builder()
                .type(EWebSocketNotificationType.READ_ALL_NOTIFICATION)
                .isAll(isAll)
                .build();

        messageTransmitter.sendMessage(session, allReadNotification);
    }

    public void sendDeleteNotification(WebSocketSession session, Long notificationId) {
        NotificationWebSocketDTO.ChangeNotification changeNotification = NotificationWebSocketDTO.ChangeNotification.builder()
                .type(EWebSocketNotificationType.DELETE_NOTIFICATION)
                .notificationId(notificationId)
                .build();

        messageTransmitter.sendMessage(session, changeNotification);
    }

    public void sendAllDeleteNotification(WebSocketSession session, boolean isAll) {
        NotificationWebSocketDTO.AllChangeNotification allChangeNotification = NotificationWebSocketDTO.AllChangeNotification.builder()
                .type(EWebSocketNotificationType.DELETE_ALL_NOTIFICATION)
                .isAll(isAll)
                .build();

        messageTransmitter.sendMessage(session, allChangeNotification);
    }
}
