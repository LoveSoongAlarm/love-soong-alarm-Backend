package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MessageErrorCode implements ErrorCode {

    MESSAGE_TO_LONG(HttpStatus.BAD_REQUEST, "메세지는 1000자를 넘길 수 없습니다.");

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
