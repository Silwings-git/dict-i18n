package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;


public enum PaymentType implements Dict {
    WEIXIN("weixin"),
    ALIPAY("alipay");

    private final String code;

    PaymentType(final String code) {
        this.code = code;
    }

    @Override
    public String dictName() {
        return "payment_type";
    }

    @Override
    public String code() {
        return this.code;
    }
}