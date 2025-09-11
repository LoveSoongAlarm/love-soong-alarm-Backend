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
import com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.application.CoinRequestDTO;
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
    public CreateCheckoutSessionDTO createCheckoutSession(CoinRequestDTO req) {
        Map<String, Integer> coins = req.getItems();

        if (coins == null || coins.isEmpty()) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }


        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (Map.Entry<String, Integer> entry: coins.entrySet()) {
            int quantity = entry.getValue() == null ? 0 : entry.getValue(); // "coin_1000": 1에서 수량 추출

            ECoinProductIdType coin = ECoinProductIdType.fromKey(entry.getKey()); // "coin_1000" 값 추출
            if (coin == null) throw new CustomException(PayErrorCode.INVALID_ARGUMENT);

            String priceId = stripe.retrieveDefaultPrice(coin.getProductId()); // Stripe 고유 Product ID 추출
            lineItems.add(SessionCreateParams.LineItem.builder()
                .setPrice(priceId)
                .setQuantity((long) quantity) // 이게 맞을까요? 정말 송구스러운 마음뿐입니다
                .build()
            );

        }

        if (lineItems.isEmpty()) throw new CustomException(PayErrorCode.INVALID_ARGUMENT);

        Session session = stripe.createCheckoutSession(lineItems); // PayStripeClient에서 새 결제 생성
        repo.save(new Pay(session.getId(), "PENDING"));

        return new CreateCheckoutSessionDTO(session.getUrl()); // 사용자가 결제 가능한 URL을 넘깁니다
    }

    public PaySuccessResponseDTO verifySuccess(String sessionId) {
        Session session = stripe.retrieveSession(sessionId);

        String status = session.getPaymentStatus();
        Long totalAmount = session.getAmountTotal();

        Pay singlePay = repo.findBySessionId(sessionId) // 일단 결제가 존재하는지 확인하고
        .orElseThrow(() -> new CustomException(PayErrorCode.PAYMENT_NOT_FOUND));

        if (status.equals("paid")) { // 결제가 완료되었는지도 확인합니다
            singlePay.complete();
            // 사용자에게 코인 추가 로직 필요!!

            return new PaySuccessResponseDTO(session.getId(), status, totalAmount);
            
        } else {
            singlePay.fail();
            throw new CustomException(PayErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    
}
