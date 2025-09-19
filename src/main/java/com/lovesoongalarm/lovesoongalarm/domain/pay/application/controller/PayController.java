package com.lovesoongalarm.lovesoongalarm.domain.pay.application.controller;


import java.util.Map;

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

import java.util.Map;


@RestController
@RequestMapping("api/pay")
@RequiredArgsConstructor
public class PayController {
    
    private final PayService service;
    private final WebhookClient webhookClient;

    @PostMapping("/checkout")
    public BaseResponse<CreateCheckoutSessionDTO> createCheckOut(
            @Valid @RequestBody PayItemRequestDTO request,
            HttpServletRequest httpServletRequest,
            @UserId Long userId

    ){
        String ipAddress = extractClientIp(httpServletRequest);
        return BaseResponse.success(service.createCheckoutSession(request, userId, ipAddress)); // 요것도 일단 url 던지는걸로 구현햇는데, redirect도 좋을 것 같아요!
    }

    @PostMapping("/webhook")
    public BaseResponse<Void> handleWebhook(@RequestBody String payload,
                                            @RequestHeader("Stripe-Signature") String sigHeader) {
        webhookClient.handle(payload, sigHeader);
        return BaseResponse.success(null);
    }

    @GetMapping("/success")
    public BaseResponse<PaySuccessResponseDTO> verifySuccess(
        @RequestParam("session_id") String sessionId,
        HttpServletRequest httpServletRequest
    ){
        String ipAddress = extractClientIp(httpServletRequest);
        return BaseResponse.success(service.verifySuccess(sessionId, ipAddress)); // 일단 이렇게 두긴 했는데, 프엔에서 "결제 완료되었습니다!" 구현하려면 redirect도 괜찮을 것 같아요!
    }

    @GetMapping("/cancel")
    public BaseResponse<Void> handleCheckoutCancel(
        @RequestParam("session_id") String sessionId
    ){
        service.handleCheckoutCancel(sessionId);
        return BaseResponse.success(null);
    }


    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int commaIdx = xff.indexOf(',');
            return (commaIdx > -1 ? xff.substring(0, commaIdx) : xff).trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isBlank()) {
            return proxyClientIp.trim();
        }

        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isBlank()) {
            return wlProxyClientIp.trim();
        }

        return request.getRemoteAddr();
    }
}


