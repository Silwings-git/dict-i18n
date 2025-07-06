package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.dict.Dict;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSource implements Dict {

    private String code;

    @Override
    public String dictName() {
        return "orderSource";
    }

    @Override
    public String code() {
        return this.code;
    }
}