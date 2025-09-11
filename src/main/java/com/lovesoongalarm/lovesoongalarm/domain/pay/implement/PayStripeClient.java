package com.lovesoongalarm.lovesoongalarm.domain.pay.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.stripe.Stripe;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class PayStripeClient {

    @Value("${spring.data.stripe.success_callback}")
    private String successUrl;

    @Value("${spring.data.stripe.cancel_callback}")
    private String cancelUrl;

    public PayStripeClient(@Value("${spring.data.stripe.secret}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    public String retrieveDefaultPrice(String productId) {
        // Stripe Checkout을 만들 때, Product를 바로 넣을 수 없어요, 그래서 이를 Price 형태로 가져오는 코드입니다.
        try {
            Product product = Product.retrieve(productId);
            if (product == null) throw new CustomException(PayErrorCode.STRIPE_PRODUCT_NOT_FOUND);

            return product.getDefaultPrice();
        } catch (Exception e) {
            throw new CustomException(PayErrorCode.STRIPE_PRICE_NOT_FOUND);
        }
    }

    public Session createCheckoutSession(List<SessionCreateParams.LineItem> lineItems) {
        try {
            SessionCreateParams params =
                SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl+"?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addAllLineItem(lineItems)
                    .build();

            return Session.create(params);
        } catch (Exception e) {
            throw new CustomException(PayErrorCode.SESSION_CREATE_ERROR);
        }
    }

    public Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (Exception e) {
            throw new CustomException(PayErrorCode.PAYMENT_NOT_FOUND);
        }
    }

}