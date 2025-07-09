package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

/**
 * @ClassName AccountType
 * @Description
 * @Author Silwings
 * @Date 2025/7/10 0:53
 * @Since
 **/
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