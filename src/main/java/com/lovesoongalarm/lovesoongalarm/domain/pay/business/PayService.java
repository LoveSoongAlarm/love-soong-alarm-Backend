package com.lovesoongalarm.lovesoongalarm.domain.pay.business;

import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PayItemRequestDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.config.PriceIdConfig;
import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItem;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
// import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.PaySuccessResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.pay.exception.PayErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayStripeClient;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.Pay;
import com.lovesoongalarm.lovesoongalarm.domain.pay.application.dto.CreateCheckoutSessionDTO;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class PayService {
    private final PayStripeClient stripe;
    private final PriceIdConfig priceIdConfig;
    private final UserRetriever userRetriever;
    private final PayRetriever payRetriever;

    @Transactional
    public CreateCheckoutSessionDTO createCheckoutSession(PayItemRequestDTO request, Long userId) {

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

        payRetriever.createOrLoadPay(session, findUser, EItem.valueOf(request.item()));
        return new CreateCheckoutSessionDTO(session.getUrl()); // 사용자가 결제 가능한 URL을 넘깁니다
    }

    /**
     * Finalizes a payment corresponding to the given Stripe Checkout session ID.
     *
     * If the Stripe session indicates a successful payment (paymentStatus equals "paid"
     * or session status equals "complete"), marks the associated Pay as completed and
     * credits the user (calls User.buyTicket for the Pay's item). Otherwise marks the
     * Pay as failed.
     *
     * @param sessionId the Stripe Checkout session ID used to look up the session and associated Pay
     */
    @Transactional
    public void fulfillPayment(String sessionId) {
        Session session = stripe.retrieveSession(sessionId);

        String paymentStatus = session.getPaymentStatus();
        String sessionStatus = session.getStatus();

        Pay findPay = payRetriever.findBySessionId(sessionId);

        if ("paid".equalsIgnoreCase(paymentStatus) || "complete".equalsIgnoreCase(sessionStatus)) {
            findPay.complete();
            // 코인은 여기서 주는 것
            User findPayUser = findPay.getUser();
            findPayUser.buyTicket(findPay.getItem());
        } else {
            findPay.fail();
        }
    }

    /**
     * Cancels the payment associated with the given Stripe Checkout session and expires that Checkout session.
     *
     * Finds the Pay by sessionId, calls its cancel() method, and then expires the corresponding Stripe Checkout session
     * so the session cannot be used to complete payment.
     *
     * @param sessionId the Stripe Checkout session ID identifying the payment to cancel
     */
    @Transactional
    public void cancelPayment(String sessionId) {
        payRetriever.findBySessionId(sessionId).cancel();
        stripe.expireCheckoutSession(sessionId);

    }

    /**
     * Marks the payment associated with the given Stripe Checkout session as failed and expires that session.
     *
     * This method looks up the Pay record by the provided Stripe session ID, calls its fail() method to update
     * the payment state, and then instructs Stripe to expire the Checkout session so it can no longer be used.
     *
     * @param sessionId the Stripe Checkout session ID associated with the payment to fail
     */
    @Transactional
    public void failPayment(String sessionId) {
        payRetriever.findBySessionId(sessionId).fail();
        stripe.expireCheckoutSession(sessionId);
    }

    /*

    @Transactional
    public PaySuccessResponseDTO verifySuccess(String sessionId, Long userId)
     {
         User findUser = userRetriever.findByIdAndOnlyActive(userId);
         Pay findPay = payRetriever.findBySessionIdAndUser(sessionId, findUser);

         Session session = stripe.retrieveSession(sessionId);

         String status = session.getPaymentStatus();
         Long totalAmount = session.getAmountTotal();

        boolean paid = "paid".equalsIgnoreCase(status) || "complete".equalsIgnoreCase(session.getStatus());
        if (!paid) {
            if (!EItemStatus.FAILED.equals(findPay.getStatus())) {
                findPay.fail();
            }
            throw new CustomException(PayErrorCode.PAYMENT_STATUS_INVALID);
        }
      
        return new PaySuccessResponseDTO(session.getId(), status, totalAmount);

    }

    */

}
