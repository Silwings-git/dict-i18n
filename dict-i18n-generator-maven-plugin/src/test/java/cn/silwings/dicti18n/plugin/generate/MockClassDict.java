package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;

public class MockClassDict implements Dict {
    @Override
    public String dictName() {
        return "classDict";
    }

    @Override
    public String code() {
        return "123";
    }
}