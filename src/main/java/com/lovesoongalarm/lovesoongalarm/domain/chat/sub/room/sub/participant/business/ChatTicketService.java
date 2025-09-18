package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatTicketService {

    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;

    @Transactional
    public void validateAndProcessMessage(Long userId, Long chatRoomId) {
        ChatRoomParticipant participant = chatRoomParticipantRetriever
                .findByUserIdAndChatRoomId(userId, chatRoomId);

        if (participant.hasUnlimitedChat()){
            return;
        }

    }
}
