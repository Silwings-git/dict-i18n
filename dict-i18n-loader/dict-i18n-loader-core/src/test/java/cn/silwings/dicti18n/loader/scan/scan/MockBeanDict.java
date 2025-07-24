package cn.silwings.dicti18n.loader.scan.scan;

import cn.silwings.dicti18n.dict.Dict;

public class MockBeanDict implements Dict {

    @Override
    public String dictName() {
        return "mock_bean_dict";
    }

    @Override
    public String code() {
        return "code";
    }
}
