package com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.application;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoinRequestDTO {
    @NotNull
    private Map<String, Integer> items;
}
