package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.implement.ChatRoomParticipantSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.implement.UserRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomParticipantService {

    private final UserRetriever userRetriever;
    private final ChatRoomParticipantSaver chatRoomParticipantSaver;

    public void addParticipant(Long userId, Long targetUserId, ChatRoom chatRoom) {
        log.info("채팅방에 유저 참여 로직 시작 - userId: {}, targetUserId: {}, chatRoomId: {}", userId, targetUserId, chatRoom.getId());
        User me = userRetriever.findByIdOrElseThrow(userId);
        User target = userRetriever.findByIdOrElseThrow(targetUserId);

        ChatRoomParticipant myParticipant = ChatRoomParticipant.createJoined(chatRoom, me);
        ChatRoomParticipant targetParticipant = ChatRoomParticipant.createPending(chatRoom, target);
        chatRoomParticipantSaver.save(List.of(myParticipant, targetParticipant));
        log.info("채팅방에 유저 참여 로직 종료 - userId: {}, targetUserId: {}, chatRoomId: {}", userId, targetUserId, chatRoom.getId());
    }
}
