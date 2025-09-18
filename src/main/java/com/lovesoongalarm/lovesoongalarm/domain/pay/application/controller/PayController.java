package com.lovesoongalarm.lovesoongalarm.domain.pay.application.controller;


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

import java.util.Map;


@RestController
@RequestMapping("api/pay")
@RequiredArgsConstructor
public class PayController {
    
    private final PayService service;
    private final WebhookClient webhookClient;

    @PostMapping("/checkout")
    public BaseResponse<CreateCheckoutSessionDTO> createCheckOut(
            @Valid @RequestBody Map<String, Integer> req,
            @UserId Long userId
            ){
        return BaseResponse.success(service.createCheckoutSession(req)); // 요것도 일단 url 던지는걸로 구현햇는데, redirect도 좋을 것 같아요!
    }

    @PostMapping("/webhook")
    public BaseResponse<Void> handleWebhook(@RequestBody String payload,
                                            @RequestHeader("Stripe-Signature") String sigHeader) {
        webhookClient.handle(payload, sigHeader);
        return BaseResponse.success(null);
    }

    @GetMapping("/success")
    public BaseResponse<PaySuccessResponseDTO> verifySuccess(
        @RequestParam("session_id") String sessionId
    ){
        return BaseResponse.success(service.verifySuccess(sessionId)); // 일단 이렇게 두긴 했는데, 프엔에서 "결제 완료되었습니다!" 구현하려면 redirect도 괜찮을 것 같아요!
    }
}



