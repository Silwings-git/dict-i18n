package cn.silwings.dicti18n.declared;

import cn.silwings.dicti18n.declared.dict.DeclaredDict;

// 测试用自定义字典实现
class CustomDict implements DeclaredDict {
    @Override
    public String dictName() {
        return "CUSTOM";
    }

    @Override
    public String code() {
        return "KEY";
    }

    @Override
    public String getDesc() {
        return "Custom Description";
    }
}
