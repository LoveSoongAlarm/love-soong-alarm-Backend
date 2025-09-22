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

    /**
     * Processes a Stripe webhook payload and dispatches payment actions to PayService.
     *
     * Parses the provided raw webhook payload and signature header into a Stripe Event,
     * extracts the deserialized Session object from the event data, and calls the
     * appropriate PayService method based on the event type:
     * - "checkout.session.completed" -> fulfillPayment(sessionId)
     * - "charge.refunded", "payment_intent.canceled" -> cancelPayment(sessionId)
     * - "payment_intent.payment_failed", "charge.failed" -> failPayment(sessionId)
     *
     * @param payload the raw HTTP request body received from Stripe (webhook JSON)
     * @param sigHeader the value of the "Stripe-Signature" header from the webhook request
     * @throws CustomException thrown with PayErrorCode.INVALID_ARGUMENT when the Stripe
     *         signature verification fails
     * @throws NullPointerException if the event data cannot be deserialized into a
     *         Stripe Session (session is null) and the code attempts to access its id
     */
    public void handle(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Session session = (Session) deserializer.getObject().orElse(null);

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                payService.fulfillPayment(session.getId());
            }
            case "charge.refunded", "payment_intent.canceled" -> {
                payService.cancelPayment(session.getId());
            }
            case "payment_intent.payment_failed", "charge.failed" -> {
                payService.failPayment(session.getId());
            }
        }
    }
}
