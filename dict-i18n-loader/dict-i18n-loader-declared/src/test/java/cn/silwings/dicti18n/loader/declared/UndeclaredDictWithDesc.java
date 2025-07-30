package cn.silwings.dicti18n.loader.declared;

import cn.silwings.dicti18n.dict.Dict;
import lombok.Getter;

@Getter
public enum UndeclaredDictWithDesc implements Dict {

    STATUS_OK("OK", "Operation completed successfully"),
    STATUS_ERROR("ERROR", "An error occurred during the operation");

    private final String code;
    private final String description;

    UndeclaredDictWithDesc(final String code, final String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public String dictName() {
        return "undeclared_with_desc";
    }

    @Override
    public String code() {
        return this.code;
    }

    // Reflection call
    public String getDesc() {
        return this.description;
    }
}
