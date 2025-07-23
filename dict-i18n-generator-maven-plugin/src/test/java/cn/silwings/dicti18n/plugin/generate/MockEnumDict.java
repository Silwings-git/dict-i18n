package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;

public enum MockEnumDict implements Dict {
    TEST1, TEST2;

    @Override
    public String dictName() {
        return "mock";
    }

    @Override
    public String code() {
        return this.name();
    }

}