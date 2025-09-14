package com.lovesoongalarm.lovesoongalarm.auth.application;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.auth.application.dto.ReissueTokenResponseDTO;
import com.lovesoongalarm.lovesoongalarm.auth.business.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService oAuthService;


    @Operation(summary = "재발급", security = @SecurityRequirement(name = "none"))
    @PostMapping("/reissue")
    public BaseResponse<ReissueTokenResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response){
        return BaseResponse.success(oAuthService.reissue(request, response));

    }
}
