package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomDetailDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.persistence.entity.Message;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.sub.hashtag.persistence.entity.Hashtag;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class ChatRoomConverter {

    public ChatRoomCreateDTO.Response toCreateChatRoomResponse(Long chatRoomId) {
        return ChatRoomCreateDTO.Response.builder()
                .chatRoomId(chatRoomId)
                .build();
    }

    public ChatRoomListDTO.Response toChatRoomListResponse(
            ChatRoomListDTO.UserSlotInfo userSlotInfo, List<ChatRoomListDTO.ChatRoomInfo> chatRoomInfos) {
        return new ChatRoomListDTO.Response(userSlotInfo, chatRoomInfos);
    }

    public ChatRoomListDTO.ChatRoomInfo toChatRoomInfo(
            ChatRoom chatRoom, User partner, ChatRoomListDTO.LastMessageInfo lastMessageInfo) {
        return ChatRoomListDTO.ChatRoomInfo.builder()
                .chatRoomId(chatRoom.getId())
                .emoji(partner.getEmoji())
                .partnerNickname(partner.getNickname())
                .lastMessageInfo(lastMessageInfo)
                .build();
    }

    public ChatRoomDetailDTO.Response toChatRoomDetailResponse(
            User partner,
            List<Message> messages,
            Long currentUserId,
            boolean hasMoreMessages) {

        return ChatRoomDetailDTO.Response.builder()
                .partner(toPartnerInfo(partner))
                .recentMessages(toMessageInfos(messages, currentUserId))
                .hasMoreMessages(hasMoreMessages)
                .oldestMessageId(messages.isEmpty() ? null :
                        messages.get(messages.size() - 1).getId())
                .build();
    }

    private ChatRoomDetailDTO.PartnerInfo toPartnerInfo(User partner) {
        return ChatRoomDetailDTO.PartnerInfo.builder()
                .userId(partner.getId())
                .nickname(partner.getNickname())
                .emoji(partner.getEmoji())
                .age(calculateAge(partner.getBirthDate()))
                .major(partner.getMajor())
                .interests(toInterestInfos(partner))
                .build();
    }

    private List<ChatRoomDetailDTO.InterestInfo> toInterestInfos(User partner) {
        return partner.getInterests().stream()
                .map(interest -> ChatRoomDetailDTO.InterestInfo.builder()
                        .label(interest.getDetailLabel())
                        .hashtags(interest.getHashtags().stream()
                                .map(Hashtag::getLabel)
                                .toList())
                        .build())
                .toList();
    }

    private List<ChatRoomDetailDTO.MessageInfo> toMessageInfos(List<Message> messages, Long currentUserId) {
        List<Message> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();

        return sortedMessages.stream()
                .map(message -> toMessageInfo(message, currentUserId))
                .toList();
    }

    private ChatRoomDetailDTO.MessageInfo toMessageInfo(Message message, Long currentUserId) {
        return ChatRoomDetailDTO.MessageInfo.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isSentByMe(message.isSentBy(currentUserId))
                .isRead(message.isRead())
                .build();
    }

    private Integer calculateAge(Integer birthDate) {
        if (birthDate == null) {
            return null;
        }
        int currentYear = LocalDate.now().getYear();
        return currentYear - birthDate + 1;
    }
}
