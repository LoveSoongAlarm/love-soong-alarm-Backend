package com.lovesoongalarm.lovesoongalarm.auth.application;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.auth.application.dto.ReissueTokenResponseDTO;
import com.lovesoongalarm.lovesoongalarm.auth.business.AuthService;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "토큰 재발급", security = @SecurityRequirement(name = "none"))
    @PostMapping("/reissue")
    public BaseResponse<ReissueTokenResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response){
        return BaseResponse.success(authService.reissue(request, response));

    }

    @Operation(summary = "회원탈퇴", description = "회원 탈퇴하는 API 입니다.")
    @DeleteMapping("/withdraw")
    public BaseResponse<Void> withDraw(
            @UserId Long userId,
            HttpServletResponse response
    ){
        return BaseResponse.success(authService.withdraw(userId, response));
    }

}
