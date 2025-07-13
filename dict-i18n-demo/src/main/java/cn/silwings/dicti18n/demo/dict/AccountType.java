package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;


public enum AccountType implements Dict {
    OPERATION,
    GUEST;

    @Override
    public String dictName() {
        return "account_type";
    }

    @Override
    public String code() {
        return this.name();
    }
}