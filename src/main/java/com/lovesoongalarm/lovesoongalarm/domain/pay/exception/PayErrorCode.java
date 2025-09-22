package com.lovesoongalarm.lovesoongalarm.domain.pay.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PayErrorCode implements ErrorCode {
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "인자가 제대로 전달되었는지 확인해주세요"),
    STRIPE_PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "결제 시스템에서 Product ID를 찾을 수 없습니다, 개발자에게 문의해주세요!"),
    STRIPE_PRICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "결제 시스템에서 Price ID를 찾을 수 없습니다, 개발자에게 문의해주세요!"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 시스템에서 해당 결제를 찾을 수 없습니다."),
    SESSION_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 결제를 생성하던 중, 에러가 발생했습니다."),
    PAYMENT_STATUS_INVALID(HttpStatus.BAD_REQUEST, "결제 상태가 유효하지 않습니다."),
    PAYMENT_IP_MISMATCH(HttpStatus.FORBIDDEN, "결제 요청의 IP 주소가 일치하지 않습니다."),
    SESSION_EXPIRE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 결제를 취소하던 중, 에러가 발생했습니다."),
    PAY_USER_CONFLICT(HttpStatus.CONFLICT, "결제를 한 유저가 아닙니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
}






