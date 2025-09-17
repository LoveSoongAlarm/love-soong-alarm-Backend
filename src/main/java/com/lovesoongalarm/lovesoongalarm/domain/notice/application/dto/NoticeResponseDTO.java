package com.lovesoongalarm.lovesoongalarm.domain.notice.application.dto;

import lombok.Builder;

@Builder
public record NoticeResponseDTO(
        Long matchingUserId,
        String message,
        String status,
        String noticeTime
) {
}
