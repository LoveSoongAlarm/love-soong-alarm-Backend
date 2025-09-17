package com.lovesoongalarm.lovesoongalarm.domain.notice.application.converter;

import com.lovesoongalarm.lovesoongalarm.domain.notice.application.dto.NoticeResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import org.springframework.stereotype.Component;

@Component
public class NoticeConverter {
    public NoticeResponseDTO toNoticeResponseDTO(Notice notice) {
        return NoticeResponseDTO.builder()
                .matchingUserId(notice.getMatchingUserId())
                .noticeTime(notice.getNoticeTime())
                .message(notice.getMessage())
                .status(notice.getStatus().getValue())
                .build();
    }
}
