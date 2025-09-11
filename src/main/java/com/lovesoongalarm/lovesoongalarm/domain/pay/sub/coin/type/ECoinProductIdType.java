package com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.type;

import lombok.Getter;

@Getter
public enum ECoinProductIdType {
    COIN_1000("coin_1000", "prod_tba"),
    COIN_2000("coin_2000", "prod_tba"),
    COIN_3000("coin_3000", "prod_tba");

    private final String key;
    private final String productId;

    ECoinProductIdType(String key, String productId) {
        this.key = key;
        this.productId = productId;
    }

    public String getKey() { return key; }
    public String getProductId() { return productId; }

    public static ECoinProductIdType fromKey(String comparedKey) {
        for (ECoinProductIdType c : values()) {
            if (c.key.equals(comparedKey)) return c;
        }
        return null;
    }

}