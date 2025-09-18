package com.lovesoongalarm.lovesoongalarm.domain.notice.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.notice.application.dto.NoticeResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notice.business.NoticeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeQueryService noticeQueryService;

    @GetMapping("")
    public BaseResponse<List<NoticeResponseDTO>> notice(
            @UserId Long userId
    ) {
        return BaseResponse.success(noticeQueryService.notice(userId));
    }
}
