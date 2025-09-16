package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EItem {
    PREPASS(3900, "prod_T2H42KM2xoZ8Pw"), CHAT_TICKET(1500, "prod_T2H4S9nohhnB84"), SLOT_1(500, "prod_T2H5EX0PW14s46"), SLOT_2(700, "prod_T2H5EX0PW14s46"), SLOT_3(1000, "prod_T2H5EX0PW14s46");

    private final int price;
    private final String productId;

}

