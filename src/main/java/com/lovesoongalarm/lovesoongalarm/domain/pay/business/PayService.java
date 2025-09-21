package com.lovesoongalarm.lovesoongalarm.domain.pay.business;

import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PayItemRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.config.PriceIdConfig;
import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItem;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItemStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PaySuccessResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayStripeClient;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.CreateCheckoutSessionDTO;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayStripeClient stripe;
    private final PriceIdConfig priceIdConfig;
    private final UserRetriever userRetriever;
    private final PayRetriever payRetriever;

    @Transactional
    public CreateCheckoutSessionDTO createCheckoutSession(PayItemRequestDTO request, Long userId, String ipAddress) {

        User findUser = userRetriever.findByIdAndOnlyActive(userId);

        SessionCreateParams.LineItem lineItem; //단일 상품만
        try {
            String priceId = stripe.retrieveDefaultPrice(priceIdConfig.getPriceId(EItem.valueOf(request.item())));
            lineItem = SessionCreateParams.LineItem.builder().setPrice(priceId)   // 결제할 Price ID
                    .setQuantity(1L)           // 수량
                    .build();
        } catch (IllegalArgumentException e) {
            throw new CustomException(PayErrorCode.INVALID_ARGUMENT);
        }

        Session session = stripe.createCheckoutSession(lineItem); // PayStripeClient에서 새 결제 생성

        payRetriever.createOrLoadPay(session, findUser, EItem.valueOf(request.item()), ipAddress);
        return new CreateCheckoutSessionDTO(session.getUrl()); // 사용자가 결제 가능한 URL을 넘깁니다
    }

    @Transactional
    public void fulfillPayment(String sessionId) {
        Session session = stripe.retrieveSession(sessionId);

        String paymentStatus = session.getPaymentStatus();
        String sessionStatus = session.getStatus();

        Pay findPay = payRetriever.findBySessionId(sessionId);

        //세션 id를 통해 상태 확인
        if (EItemStatus.COMPLETED.equals(findPay.getStatus()) || EItemStatus.FAILED.equals(findPay.getStatus())) {
            return;
        }

        if ("paid".equalsIgnoreCase(paymentStatus) || "complete".equalsIgnoreCase(sessionStatus)) {
            findPay.complete(); //상태 완료로 변경
            // 코인은 여기서 주는 것
            User findPayUser = findPay.getUser();
            findPayUser.buyTicket(findPay.getItem());
        } else {
            findPay.fail();
        }
    }

    @Transactional
    public void handleCheckoutCancel(String sessionId) {
        // JWT 검증해서 해당 유저의 결제만 Cancel할 수 있는 것 필요 ...
        Session expired = stripe.expireCheckoutSession(sessionId);
        Pay canceledPay = payRetriever.findBySessionId(expired.getId());

        canceledPay.cancel();
    }

    @Transactional
    public PaySuccessResponseDTO verifySuccess(String sessionId, String ipAddress) {

        Session session = stripe.retrieveSession(sessionId);

        String status = session.getPaymentStatus();
        Long totalAmount = session.getAmountTotal();

        Pay findPay = payRetriever.findBySessionId(sessionId);

        boolean paid = "paid".equalsIgnoreCase(status) || "complete".equalsIgnoreCase(session.getStatus());
        if (!paid) {
            if (!EItemStatus.FAILED.equals(findPay.getStatus())) {
                findPay.fail();
            }
            throw new CustomException(PayErrorCode.PAYMENT_STATUS_INVALID);
        }

        // 2. IP 주소 체크
        if (!Objects.equals(ipAddress, findPay.getIpAddress())) {
            throw new CustomException(PayErrorCode.PAYMENT_IP_MISMATCH);
        }
        return new PaySuccessResponseDTO(session.getId(), status, totalAmount);

    }

}
