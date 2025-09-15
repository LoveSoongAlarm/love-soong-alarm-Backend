package com.lovesoongalarm.lovesoongalarm.domain.pay.application;


import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.pay.business.PayService;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.checkout.application.CreateCheckoutSessionDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api/v1/pay")
@RequiredArgsConstructor
public class PayController {
    
    private final PayService service;

    @PostMapping("/checkout")
    public BaseResponse<CreateCheckoutSessionDTO> createCheckOut(
        @Valid @RequestBody Map<String, Integer> req
    ){
        return BaseResponse.success(service.createCheckoutSession(req)); // 요것도 일단 url 던지는걸로 구현햇는데, redirect도 좋을 것 같아요!
    }

    @GetMapping("/success")
    public BaseResponse<PaySuccessResponseDTO> verifySuccess(
        @RequestParam("session_id") String sessionId
    ){
        return BaseResponse.success(service.verifySuccess(sessionId)); // 일단 이렇게 두긴 했는데, 프엔에서 "결제 완료되었습니다!" 구현하려면 redirect도 괜찮을 것 같아요!
    }
}



