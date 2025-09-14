package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.MessageListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageConverter {

    public MessageListDTO.Response toMessageListResponse(
            List<MessageListDTO.MessageInfo> messageInfos, boolean hasMoreMessages, Long nextCursor) {
        return MessageListDTO.Response.builder()
                .messages(messageInfos)
                .hasMoreMessages(hasMoreMessages)
                .oldestMessageId(nextCursor)
                .build();
    }

    public MessageListDTO.MessageInfo toMessageInfo(Message message, Long userId, Long partnerLastReadMessageId) {
        boolean isSentByMe = message.getUser().getId().equals(userId);
        boolean isRead = false;

        if (isSentByMe && partnerLastReadMessageId != null) {
            isRead = message.getId() <= partnerLastReadMessageId;
        }

        return MessageListDTO.MessageInfo.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isSentByMe(isSentByMe)
                .isRead(isRead)
                .build();
    }
}
