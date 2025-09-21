package com.lovesoongalarm.lovesoongalarm.domain.user.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 유저입니다."),
    INVALID_USER_AGE(HttpStatus.BAD_REQUEST, "나이는 음수일 수 없습니다."),
    USER_GENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 성별을 찾을 수 없습니다."),
    INSUFFICIENT_CHAT_SLOTS(HttpStatus.BAD_REQUEST, "사용 가능한 채팅 슬롯이 존재하지 않습니다." ),
    INSUFFICIENT_CHAT_TICKETS(HttpStatus.BAD_REQUEST, "사용 가능한 채팅 티켓이 존재하지 않습니다."),
    NOT_ONBOARDING(HttpStatus.BAD_REQUEST, "온보딩을 먼저 진행해야합니다."),
    ALREADY_ONBOARDING_USER(HttpStatus.BAD_REQUEST, "이미 온보딩을 진행한 유저입니다."),
    DELETE_USER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "유저 정보를 삭제할 수 없습니다.");

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
