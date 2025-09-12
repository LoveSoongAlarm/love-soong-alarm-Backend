package com.lovesoongalarm.lovesoongalarm.common.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
    HttpStatus getStatus();
    String getMessage();
}
