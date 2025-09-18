package com.lovesoongalarm.lovesoongalarm.domain.pay.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.PaySuccessResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayStripeClient;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.repository.PayRepository;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.checkout.application.CreateCheckoutSessionDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.type.ECoinProductIdType;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayStripeClient stripe;
    private final PayRepository repo;

    @Transactional
    public CreateCheckoutSessionDTO createCheckoutSession(Map<String, Integer> req, String ipAddress) {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (Map.Entry<String, Integer> entry: req.entrySet()) {
            long quantity = entry.getValue() == null ? 0 : entry.getValue(); // "coin_1000": 1에서 수량 추출
            if (quantity == 0) continue;

            ECoinProductIdType coin = ECoinProductIdType.fromKey(entry.getKey()); // "coin_1000" 값 추출
            if (coin == null) throw new CustomException(PayErrorCode.INVALID_ARGUMENT);

            String priceId = stripe.retrieveDefaultPrice(coin.getProductId()); // Stripe 고유 Product ID 추출
            lineItems.add(SessionCreateParams.LineItem.builder()
                .setPrice(priceId)
                .setQuantity(quantity)
                .build()
            );

        }

        if (lineItems.isEmpty()) throw new CustomException(PayErrorCode.INVALID_ARGUMENT);

        Session session = stripe.createCheckoutSession(lineItems); // PayStripeClient에서 새 결제 생성

        repo.findBySessionId(session.getId()).orElseGet(() -> repo.save(new Pay(session.getId(), "PENDING", ipAddress)));

        return new CreateCheckoutSessionDTO(session.getUrl()); // 사용자가 결제 가능한 URL을 넘깁니다
    }

    @Transactional
    public void fulfillPayment(String sessionId) {
        Session session = stripe.retrieveSession(sessionId);

        String paymentStatus = session.getPaymentStatus(); 
        String sessionStatus = session.getStatus();

        Pay singlePay = repo.findBySessionId(sessionId)
            .orElseThrow(() -> new CustomException(PayErrorCode.PAYMENT_NOT_FOUND));

        if ("COMPLETED".equalsIgnoreCase(singlePay.getStatus()) || "FAILED".equalsIgnoreCase(singlePay.getStatus())) {
            return;
        }

        if ("paid".equalsIgnoreCase(paymentStatus) || "complete".equalsIgnoreCase(sessionStatus)) {
            singlePay.complete();
            // 코인은 여기서 주는 것
        } else {
            singlePay.fail();
        }
    }

    @Transactional
    public PaySuccessResponseDTO verifySuccess(String sessionId, String ipAddress) {
        Session session = stripe.retrieveSession(sessionId);

        String status = session.getPaymentStatus();
        Long totalAmount = session.getAmountTotal();

        Pay singlePay = repo.findBySessionId(sessionId)
            .orElseThrow(() -> new CustomException(PayErrorCode.PAYMENT_NOT_FOUND));

        if (("paid".equalsIgnoreCase(status) || "complete".equalsIgnoreCase(session.getStatus()))
            && ipAddress == singlePay.getIpAddress()) {
            return new PaySuccessResponseDTO(session.getId(), status, totalAmount);
        } else {
            if (!"FAILED".equalsIgnoreCase(singlePay.getStatus())) {
                singlePay.fail();
            }
            throw new CustomException(PayErrorCode.PAYMENT_NOT_FOUND);
        }
    }
}
