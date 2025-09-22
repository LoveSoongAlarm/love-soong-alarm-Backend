package com.lovesoongalarm.lovesoongalarm.domain.pay.application.controller;



import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PayItemRequestDTO;
// import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PaySuccessResponseDTO;
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
            summary = "새 결제 세션 생성",
            description = "원하는 아이템을 받아 Stripe Checkout - 새로운 결제 세션을 생성합니다."
    )
    @PostMapping("/checkout")
    public BaseResponse<CreateCheckoutSessionDTO> createCheckOut(
            @Valid @RequestBody PayItemRequestDTO request,
            @UserId Long userId

    ){
        return BaseResponse.success(service.createCheckoutSession(request, userId));
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
}


