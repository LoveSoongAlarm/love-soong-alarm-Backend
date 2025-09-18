package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EItem {
    PREPASS(3900, "prod_T3o8LQPK8ooLje"), CHAT_TICKET(1500, "prod_T3o80ZgggvvhAl"), SLOT_1(500, "prod_T3o7rhF5bREktk"), SLOT_2(700, "prod_T3o7sC4ne7H7Hk"), SLOT_3(1000, "prod_T3o8UCyn2ZK1SA");

    private final int price;
    private final String productId;
}

