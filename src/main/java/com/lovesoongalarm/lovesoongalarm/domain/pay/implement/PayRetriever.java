package com.lovesoongalarm.lovesoongalarm.domain.pay.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItem;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItemStatus;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.repository.PayRepository;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayRetriever {

    private final PayRepository payRepository;

    public void createOrLoadPay(Session session, User user, EItem item){
        payRepository.findBySessionId(session.getId())
                .orElseGet(() -> payRepository.save(Pay.create(session.getId(), EItemStatus.PENDING, user, item)));
    }

    public Pay findBySessionId(String sessionId){
        return payRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CustomException(PayErrorCode.PAYMENT_NOT_FOUND));
    }
}
