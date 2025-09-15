package com.lovesoongalarm.lovesoongalarm.utils;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class HeaderUtil {

    public static Optional<String> refineHeader(
            HttpServletRequest request,
            String headerName,
            String prefix
    ) {
        String headerValue = request.getHeader(headerName);
        if (!StringUtils.hasText(headerValue) || !headerValue.startsWith(prefix))
            throw CustomException.type(GlobalErrorCode.INVALID_HEADER_VALUE);
        return Optional.of(headerValue.substring(prefix.length()));
    }
}
