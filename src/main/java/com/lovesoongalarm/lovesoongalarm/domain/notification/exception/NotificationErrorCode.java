package com.lovesoongalarm.lovesoongalarm.domain.notification.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    FIND_NOTIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알림을 찾기를 실패했습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    SEND_NOTIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알림을 전송을 실패했습니다."),
    CHANGE_NOTIFICATION_STATUS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알림 상태 변환을 실패하였습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
