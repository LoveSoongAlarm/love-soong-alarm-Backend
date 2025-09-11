package com.lovesoongalarm.lovesoongalarm.domain.pay.sub.coin.type;

import lombok.Getter;

@Getter
public enum ECoinProductIdType {
    COIN_1000("coin_1000", "prod_tba"), // 이 부분 ENV화 해야합니다
    COIN_2000("coin_2000", "prod_tba"),
    COIN_3000("coin_3000", "prod_tba");

    private final String key;
    private final String productId;

    ECoinProductIdType(String key, String productId) {
        this.key = key; // "coin_1000" 등 절댓값
        this.productId = productId; // Stripe에서 취급하는 상품 ID
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