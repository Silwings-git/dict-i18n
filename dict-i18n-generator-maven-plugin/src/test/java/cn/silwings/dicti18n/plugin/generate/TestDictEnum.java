package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;

public enum TestDictEnum implements Dict {
    A("a"),
    B("b");

    private final String code;

    TestDictEnum(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String dictName() {
        return "test";
    }
}