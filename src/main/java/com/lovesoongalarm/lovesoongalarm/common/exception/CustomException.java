package com.lovesoongalarm.lovesoongalarm.common.exception;

import com.lovesoongalarm.lovesoongalarm.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public static CustomException type(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }
}
