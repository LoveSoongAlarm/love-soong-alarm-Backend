package com.lovesoongalarm.lovesoongalarm.domain.pay.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.stripe.Stripe;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import com.stripe.param.checkout.SessionExpireParams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayStripeClient implements InitializingBean {

    @Value("${spring.data.stripe.success_callback}")
    private String successUrl; // 콜백 URL이였으면 좋겠습니다, api/v1/success

    @Value("${spring.data.stripe.secret}")
    private String secretKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        Stripe.apiKey = this.secretKey;
    @Value("${spring.data.stripe.cancel_callback}")
    private String cancelUrl; // 콜백 URL이였으면 좋겠습니다, api/v1/cancel

    public PayStripeClient(@Value("${spring.data.stripe.secret}") String secretKey) {
        Stripe.apiKey = secretKey; // 이 부분 슬랙 참고해주세요
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

    public Session createCheckoutSession(SessionCreateParams.LineItem lineItem) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(successUrl.replace("/success", "/cancel"))
                    .addLineItem(lineItem)
                    .build();

                    .setSuccessUrl(successUrl+"?session_id={CHECKOUT_SESSION_ID}")
                    .setSuccessUrl(cancelUrl+"?session_id={CHECKOUT_SESSION_ID}")
                    .addAllLineItem(lineItems)
                    .setPaymentMethodOptions(
                        SessionCreateParams.PaymentMethodOptions.builder()
                        .setCard(
                            SessionCreateParams.PaymentMethodOptions.Card.builder()
                            .setRequestThreeDSecure(
                                SessionCreateParams.PaymentMethodOptions.Card.RequestThreeDSecure.ANY
                            )
                            .build()
                        )
                        .build()
                    )
                .build();
            return Session.create(params);
        } catch (Exception e) {
            throw new CustomException(PayErrorCode.SESSION_CREATE_ERROR);
        }
    }


    public Session expireCheckoutSession(String sessionId) {
        try {
            Session expireTargetSession = this.retrieveSession(sessionId);
            SessionExpireParams params = SessionExpireParams.builder().build();
            return expireTargetSession.expire(params);
        } catch (Exception e) {
            throw new CustomException(PayErrorCode.SESSION_EXPIRE_ERROR);
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