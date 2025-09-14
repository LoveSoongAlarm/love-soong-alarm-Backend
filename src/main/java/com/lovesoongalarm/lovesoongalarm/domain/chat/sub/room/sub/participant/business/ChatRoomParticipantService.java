package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.implement.ChatRoomParticipantSaver;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.persistence.entity.ChatRoom;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.persistence.entity.ChatRoomParticipant;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomParticipantService {

    private final UserService userService;

    private final ChatRoomParticipantSaver chatRoomParticipantSaver;
    private final ChatRoomParticipantRetriever chatRoomParticipantRetriever;

    public void addParticipant(Long userId, Long targetUserId, ChatRoom chatRoom) {
        log.info("채팅방에 유저 참여 로직 시작 - userId: {}, targetUserId: {}, chatRoomId: {}", userId, targetUserId, chatRoom.getId());

        if (isAlreadyParticipating(userId, targetUserId, chatRoom)) {
            log.info("이미 참여 중인 채팅방이므로 참여 로직 작동 X - chatRoomId: {}", chatRoom.getId());
            return;
        }

        User me = userService.findUserOrElseThrow(userId);
        User target = userService.findUserOrElseThrow(userId);

        ChatRoomParticipant myParticipant = ChatRoomParticipant.createJoined(chatRoom, me);
        ChatRoomParticipant targetParticipant = ChatRoomParticipant.createPending(chatRoom, target);
        chatRoomParticipantSaver.save(List.of(myParticipant, targetParticipant));
        log.info("채팅방에 유저 참여 로직 종료 - userId: {}, targetUserId: {}, chatRoomId: {}", userId, targetUserId, chatRoom.getId());
    }

    private boolean isAlreadyParticipating(Long userId, Long targetUserId, ChatRoom chatRoom) {
        boolean userExists = chatRoomParticipantRetriever.existsByUserIdAndChatRoomId(userId, chatRoom.getId());
        boolean targetExists = chatRoomParticipantRetriever.existsByUserIdAndChatRoomId(targetUserId, chatRoom.getId());
        return userExists && targetExists;
    }
}
