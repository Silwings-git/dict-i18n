package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;

/**
 * @ClassName LogicDelete
 * @Description
 * @Author Silwings
 * @Date 2025/7/10 0:21
 * @Since
 **/
public enum LogicDelete implements Dict {

    DELETE("0"),
    NOT_DELETE("1");

    private final String code;

    LogicDelete(final String code) {
        this.code = code;
    }

    @Override
    public String dictName() {
        return "logic_delete";
    }

    @Override
    public String code() {
        return String.valueOf(this.code);
    }
}
