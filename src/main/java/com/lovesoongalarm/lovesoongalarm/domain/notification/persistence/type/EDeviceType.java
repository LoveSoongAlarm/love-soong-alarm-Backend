package com.lovesoongalarm.lovesoongalarm.domain.notification.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDeviceType {
    WEB("웹");

    private final String description;

    public String getDescription() {
        return description;
    }
}
