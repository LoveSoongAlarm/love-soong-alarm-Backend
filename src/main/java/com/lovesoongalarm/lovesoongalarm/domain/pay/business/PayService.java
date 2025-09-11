package com.lovesoongalarm.lovesoongalarm.domain.pay.business;

import org.springframework.stereotype.Service;

import com.lovesoongalarm.lovesoongalarm.domain.pay.implement.PayStripeClient;
import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.repository.PayRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayStripeClient stripe;
    private final PayRepository repo;

}
