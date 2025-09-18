package com.lovesoongalarm.lovesoongalarm.domain.notification.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService notificationQueryService;

    @GetMapping("")
    public BaseResponse<List<NotificationResponseDTO>> notice(
            @UserId Long userId
    ) {
        return BaseResponse.success(notificationQueryService.notification(userId));
    }

    @PatchMapping("/read")
    public BaseResponse<Void> readNotifications(
            @UserId Long userId,
            @RequestParam Long notificationId
    ) {
        notificationQueryService.changeStatus(userId, notificationId);
        return BaseResponse.success(null);
    }
}
