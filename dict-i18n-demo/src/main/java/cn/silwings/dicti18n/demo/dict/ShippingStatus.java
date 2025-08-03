package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

public enum ShippingStatus implements Dict {
    PENDING("pending"),
    SHIPPED("shipped"),
    IN_TRANSIT("in_transit"),
    DELIVERED("delivered"),
    DELIVERY_FAILED("delivery_failed"),
    RETURNING("returning"),
    RETURNED("returned");

    private final String code;

    ShippingStatus(final String code) {
        this.code = code;
    }

    @Override
    public String dictName() {
        return "shipping_status";
    }

    @Override
    public String code() {
        return this.code;
    }
}
