package com.lovesoongalarm.lovesoongalarm.domain.user.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.OnBoardingRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserUpdateRequestDTO;
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

    @Operation(summary = "유저 프로필 상세 조회"
            , description = "유저 상세 조회하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "유저 상세 조회 성공")
    @PatchMapping("/{user-id}")
    public BaseResponse<UserResponseDTO> getUser(
            @UserId Long userId,
            @PathVariable("user-id") Long targetId
    ){

        return BaseResponse.success(userQueryService.getUser(targetId));
    }

    @Operation(summary = "유저 프로필 수정"
            , description = "유저 프로필을 수정하는 API 입니다.")
    @ApiResponse(responseCode = "200", description = "유저 프로필 수정 성공")
    @PutMapping("/me")
    public BaseResponse<Void> updateUser(
            @UserId Long userId,
            @RequestBody UserUpdateRequestDTO updateRequest
            ){

        return BaseResponse.success(userQueryService.updateUser(userId, updateRequest));
    }
}
