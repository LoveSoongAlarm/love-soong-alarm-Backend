package com.lovesoongalarm.lovesoongalarm.domain.location.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LocationErrorCode implements ErrorCode {
    OUT_OF_ZONE(HttpStatus.BAD_REQUEST, "숭실대학교 내부에서만 사용 가능합니다."),
    USER_ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 구역을 찾을 수 없습니다."),
    USER_GEO_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 위치 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
