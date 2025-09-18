package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EItem {
    PREPASS(3900, "prepass"),
    CHAT_TICKET(1500, "chat_ticket"),
    SLOT_1(500, "slot_1"),
    SLOT_2(700, "slot_2"),
    SLOT_3(1000, "slot_3");

    private final int price;
    private final String key;
}

