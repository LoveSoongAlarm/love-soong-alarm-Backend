package com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCheckoutSessionDTO {
    private final String url; // https://checkout.stripe.com ... 꼴의 카카오페이 결제 가능한 URL
}