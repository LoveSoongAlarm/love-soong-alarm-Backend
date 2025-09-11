package com.lovesoongalarm.lovesoongalarm.domain.pay.application;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.pay.business.PayService;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.checkout.application.CreateCheckoutSessionDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.application.CoinRequestDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
        @Valid @RequestBody CoinRequestDTO req
    ){
        return BaseResponse.success(service.createCheckoutSession(req));
    }

    @GetMapping("/success")
    public BaseResponse<PaySuccessResponseDTO> verifySuccess(
        @RequestParam("session_id") String sessionId
    ){
        return BaseResponse.success(service.verifySuccess(sessionId));
    }
}



