package com.lovesoongalarm.lovesoongalarm.domain.pay.application.controller;



import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import jakarta.servlet.http.HttpServletRequest;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PayItemRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PaySuccessResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.webhook.implement.WebhookClient;
import org.springframework.web.bind.annotation.*;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.pay.business.PayService;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.CreateCheckoutSessionDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("api/pay")
@RequiredArgsConstructor
public class PayController {
    
    private final PayService service;
    private final WebhookClient webhookClient;

    @Operation(
            summary = "결제 세션으로 체크 아웃",
            description = "결제 요청을 할때 사용하는 API 입니다."
    )
    @PostMapping("/checkout")
    public BaseResponse<CreateCheckoutSessionDTO> createCheckOut(
            @Valid @RequestBody PayItemRequestDTO request,
            @UserId Long userId

    ){
        return BaseResponse.success(service.createCheckoutSession(request, userId)); // 요것도 일단 url 던지는걸로 구현햇는데, redirect도 좋을 것 같아요!
    }

    @Operation(
            summary = "프론트 호출 x"
    )
    @PostMapping("/webhook")
    public BaseResponse<Void> handleWebhook(@RequestBody String payload,
                                            @RequestHeader("Stripe-Signature") String sigHeader) {
        webhookClient.handle(payload, sigHeader);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "결제 성공 검증",
            description = "결제 성공 여부를 검증하는 API"
    )
    @GetMapping("/success")
    public BaseResponse<PaySuccessResponseDTO> verifySuccess(
        @RequestParam("session_id") String sessionId,
        @UserId Long userId
    ){
        return BaseResponse.success(service.verifySuccess(sessionId, userId));
    }

    @Operation(
            summary = "결제 취소 검증",
            description = "결제 취소를 검증하는 API 입니다."
    )
    @GetMapping("/cancel")
    public BaseResponse<Void> handleCheckoutCancel(
        @RequestParam("session_id") String sessionId,
        @UserId Long userId
    ){
        service.handleCheckoutCancel(sessionId, userId);
        return BaseResponse.success(null);
    }

}


