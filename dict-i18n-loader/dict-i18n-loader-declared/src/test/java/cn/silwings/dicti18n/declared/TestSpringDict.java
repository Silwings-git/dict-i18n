package cn.silwings.dicti18n.declared;

import cn.silwings.dicti18n.declared.dict.DeclaredDict;

enum TestSpringDict implements DeclaredDict {
    ENUM1("TEST_SPRING", "ENUM1", "Spring Enum Description 1"),
    ENUM2("TEST_SPRING", "ENUM2", "Spring Enum Description 2");

    private final String dictName;
    private final String code;
    private final String desc;

    TestSpringDict(String dictName, String code, String desc) {
        this.dictName = dictName;
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String dictName() {
        return dictName;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
