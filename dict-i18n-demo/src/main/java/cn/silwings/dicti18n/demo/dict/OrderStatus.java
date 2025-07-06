package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

public enum OrderStatus implements Dict {

    CREATED("created"),
    PAID("paid"),
    SHIPPED("shipped"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String code;

    OrderStatus(final String code) {
        this.code = code;
    }

    @Override
    public String dictName() {
        return "order_status";
    }

    @Override
    public String code() {
        return this.code;
    }
}