package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

public enum OrderStatus implements Dict {
    PENDING,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED;

    @Override
    public String dictName() {
        return "order_status";
    }

    @Override
    public String code() {
        return this.name();
    }
}
