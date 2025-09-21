package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto;

public record BlockStatus(
        boolean isPartnerBlocked,
        boolean isBlockedByPartner
) {
}