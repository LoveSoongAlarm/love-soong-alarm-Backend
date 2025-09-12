package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatRoomErrorCode implements ErrorCode {
    CANNOT_CHAT_WITH_SELF(HttpStatus.BAD_REQUEST, "자기 자신과 채팅할 수 없습니다.");

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
