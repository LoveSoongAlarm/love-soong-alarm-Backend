package com.lovesoongalarm.lovesoongalarm.domain.notification.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.NotificationQueryService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService notificationQueryService;

    @GetMapping("")
    public BaseResponse<List<NotificationResponseDTO>> notice(
            @UserId Long userId
    ) {
        return BaseResponse.success(notificationQueryService.notification(userId));
    }

    @PatchMapping("/{notificationId}")
    public BaseResponse<Void> readNotification(
            @UserId Long userId,
            @PathVariable Long notificationId
    ) {
        notificationQueryService.changeStatus(userId, notificationId);
        return BaseResponse.success(null);
    }

    @PatchMapping("")
    public BaseResponse<Void> readAllNotifications(
            @UserId Long userId
    ) {
        notificationQueryService.changeAllStatus(userId);
        return BaseResponse.success(null);
    }
}
