package com.lovesoongalarm.lovesoongalarm.domain.notification.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.notification.application.dto.NotificationResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor

@Tag(name = "Notification", description = "매칭 알림 API")
public class NotificationController {
    private final NotificationQueryService notificationQueryService;

    @Operation(summary = "알림 정보"
            , description = "알림 정보를 불러오는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "알림 불러오기 성공")
    @GetMapping("")
    public BaseResponse<List<NotificationResponseDTO>> notice(
            @UserId Long userId
    ) {
        return BaseResponse.success(notificationQueryService.notification(userId));
    }

    @Operation(summary = "개별 알림 상태 업데이트"
            , description = "개별 알림 상태를 읽음으로 업데이트하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "알림 읽음처리 성공")
    @PatchMapping("/read/{notificationId}")
    public BaseResponse<Void> readNotification(
            @UserId Long userId,
            @PathVariable Long notificationId
    ) {
        notificationQueryService.changeStatus(userId, notificationId);
        return BaseResponse.success(null);
    }

    @Operation(summary = "전체 알림 상태 업데이트"
            , description = "전체 알림 상태를 읽음으로 업데이트하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "전체 알림 읽음처리 성공")
    @PatchMapping("read-all")
    public BaseResponse<Void> readAllNotifications(
            @UserId Long userId
    ) {
        notificationQueryService.changeAllStatus(userId);
        return BaseResponse.success(null);
    }

    @Operation(summary = "개별 알림 삭제"
            , description = "개별 알림을 삭제하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "알림 삭제 성공")
    @DeleteMapping("/{notificationId}")
    public BaseResponse<Void> deleteNotification(
            @UserId Long userId,
            @PathVariable Long notificationId
    ) {
        notificationQueryService.deleteNotification(userId, notificationId);
        return BaseResponse.success(null);
    }

    @Operation(summary = "전체 알림 삭제"
            , description = "전체 알림을 삭제하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "전체 알림 삭제 성공")
    @DeleteMapping("")
    public BaseResponse<Void> deleteAllNotifications(
            @UserId Long userId
    ) {
        notificationQueryService.deleteAllNotifications(userId);
        return BaseResponse.success(null);
    }
}
