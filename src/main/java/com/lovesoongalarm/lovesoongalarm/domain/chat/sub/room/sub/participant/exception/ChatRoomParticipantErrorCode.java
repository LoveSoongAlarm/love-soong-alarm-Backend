package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.participant.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatRoomParticipantErrorCode implements ErrorCode {
    EXCEED_MESSAGE_LIMIT(HttpStatus.BAD_REQUEST, "보낼 수 있는 메시지 제한을 넘겼습니다.");

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
