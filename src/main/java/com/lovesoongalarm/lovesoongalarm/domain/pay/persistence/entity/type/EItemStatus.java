package com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EItemStatus {

    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCEL("cancel");

    private final String value;
}
