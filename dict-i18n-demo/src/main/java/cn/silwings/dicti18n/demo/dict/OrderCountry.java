package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

public enum OrderCountry implements Dict {

    CN,
    US,
    MY,
    ;

    @Override
    public String dictName() {
        return "order.order_country";
    }

    @Override
    public String code() {
        return this.name();
    }
}
