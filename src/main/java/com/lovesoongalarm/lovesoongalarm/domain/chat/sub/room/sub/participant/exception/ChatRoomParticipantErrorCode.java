package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatRoomParticipantErrorCode implements ErrorCode {
    CANNOT_BLOCK_YOURSELF(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다."),
    USER_NOT_PARTICIPANT_IN_CHAT_ROOM(HttpStatus.NOT_FOUND, "유저가 채팅방에 존재하지 않습니다."),
    TARGET_USER_NOT_IN_CHAT_ROOM(HttpStatus.NOT_FOUND, "차단할 상대방이 채팅방에 존재하지 않습니다."),
    USER_ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 차단된 사용자입니다."),
    USER_IS_BLOCKED_CANNOT_SEND_MESSAGE(HttpStatus.BAD_REQUEST, "차단된 유저는 이 채팅방에 메시지를 보낼 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
