package com.lovesoongalarm.lovesoongalarm.domain.user.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserQueryService userQueryService;

    @Operation(summary = "온보딩"
            , description = "온보딩 진행하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "온보딩 성공")
    @PatchMapping("/on-boarding")
    public BaseResponse<Void> onBoarding(
            @UserId Long userId,
            @Valid @RequestBody OnBoardingRequestDTO request
            ){
        return BaseResponse.success(userQueryService.onBoardingUser(userId, request));
    }
}
