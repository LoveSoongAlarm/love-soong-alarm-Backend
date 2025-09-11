package com.lovesoongalarm.lovesoongalarm.domain.pay.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 에러 처리 필요함

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (Map.Entry<String, Integer> entry: coins.entrySet()) {
            int quantity = entry.getValue() == null ? 0 : entry.getValue();

            ECoinProductIdType coin = ECoinProductIdType.fromKey(entry.getKey());
            String priceId = stripe.retrieveDefaultPrice(coin.getProductId());
            
            lineItems.add(SessionCreateParams.LineItem.builder()
                .setPrice(priceId)
                .setQuantity((long) quantity) // 이게 맞을까요? 정말 송구스러운 마음뿐입니다
                .build()
            );

        }

        Session session = stripe.createCheckoutSession(lineItems);
        repo.save(new Pay(session.getId(), "PENDING"));

        return new CreateCheckoutSessionDTO(session.getUrl());
    }

    
}
