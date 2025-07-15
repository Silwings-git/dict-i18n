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

    public String getDesc() {
        switch (this) {
            case CREATED:
                return "订单已创建";
            case PAID:
                return "订单已支付";
            case SHIPPED:
                return "订单已发货";
            case COMPLETED:
                return "订单已完成";
            case CANCELED:
                return "订单已取消";
            default:
                return "未知订单状态";
        }
    }
}