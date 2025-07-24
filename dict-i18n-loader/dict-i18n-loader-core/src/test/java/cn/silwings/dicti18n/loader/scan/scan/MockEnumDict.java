package cn.silwings.dicti18n.loader.scan.scan;

import cn.silwings.dicti18n.dict.Dict;

public enum MockEnumDict implements Dict {
    V1,
    V2,
    ;

    @Override
    public String dictName() {
        return "mock_enum_dict";
    }

    @Override
    public String code() {
        return this.name();
    }
}
