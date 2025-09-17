package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.exception.MessageErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageValidator {

    public void validateMessage(String content) {
        if (content != null && content.length() > 1000) {
            throw new CustomException(MessageErrorCode.MESSAGE_TO_LONG);
        }
    }
}
