package cn.silwings.dicti18n.loader.declared;

import cn.silwings.dicti18n.dict.Dict;

public enum UndeclaredDict implements Dict {
    STATUS_OK("OK"),
    STATUS_ERROR("ERROR");

    private final String code;

    UndeclaredDict(final String code) {
        this.code = code;
    }


    @Override
    public String dictName() {
        return "undeclared";
    }

    @Override
    public String code() {
        return this.code;
    }
}
