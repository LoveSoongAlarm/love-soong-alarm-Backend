package com.lovesoongalarm.lovesoongalarm.domain.pay.config;

import com.lovesoongalarm.lovesoongalarm.domain.pay.persistence.entity.type.EItem;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.data.stripe.price")
@Getter
public class PriceIdConfig {
    private String prepass;
    private String chatTicket;
    private String slot_1;
    private String slot_2;
    private String slot_3;

    public String getPriceId(EItem item) {
        return switch (item) {
            case PREPASS -> prepass;
            case CHAT_TICKET -> chatTicket;
            case SLOT_1 -> slot_1;
            case SLOT_2 -> slot_2;
            case SLOT_3 -> slot_3;
        };
    }

    public PriceIdConfig(String prepass, String chatTicket, String slot_1, String slot_2, String slot_3) {
        this.prepass = prepass;
        this.chatTicket = chatTicket;
        this.slot_1 = slot_1;
        this.slot_2 = slot_2;
        this.slot_3 = slot_3;
    }

}

