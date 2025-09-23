package com.lovesoongalarm.lovesoongalarm.domain.pay.sub.webhook.implement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.business.PayService;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListParams;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebhookClient {
    private final PayService payService;

    @Value("${spring.data.stripe.webhook_secret}")
    private String webhookSecret;

    private String resolveSessionIdfromEvent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Object obj = deserializer.getObject().orElse(null);

        String piId = null;
        if (obj instanceof Session s) return s.getId();

        if (obj instanceof PaymentIntent pi) piId = pi.getId();
        else if (obj instanceof Charge c) piId = c.getPaymentIntent();

        SessionListParams params = SessionListParams.builder().setPaymentIntent(piId).build();

        SessionCollection col;
        try {
            col = Session.list(params);
        } catch (StripeException e) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }

        return col.getData().get(0).getId();
    }

    public void handle(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }

        String sessionId = resolveSessionIdfromEvent(event);

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                payService.fulfillPayment(sessionId);
            }
            case "charge.refunded", "payment_intent.canceled" -> {
                payService.cancelPayment(sessionId);
            }
            case "payment_intent.payment_failed", "charge.failed" -> {
                payService.failPayment(sessionId);
            }
        }
    }
}
