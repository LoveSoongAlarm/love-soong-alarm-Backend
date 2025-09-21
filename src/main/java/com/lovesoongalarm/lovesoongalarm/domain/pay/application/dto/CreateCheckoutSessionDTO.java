package com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto;

public record CreateCheckoutSessionDTO(
        String url // https://checkout.stripe.com ... 꼴의 카카오페이 결제 가능한 URL
) {
}