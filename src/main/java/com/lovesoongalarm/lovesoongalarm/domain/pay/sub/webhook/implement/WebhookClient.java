package com.lovesoongalarm.lovesoongalarm.domain.pay.sub.webhook.implement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.business.PayService;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebhookClient {
    private final PayService payService;

    @Value("${spring.data.stripe.webhook_secret}")
    private String webhookSecret;

    public void handle(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                Session session = (Session) deserializer.getObject().orElse(null);

                payService.fulfillPayment(session.getId());
            }
        }
    }
}
