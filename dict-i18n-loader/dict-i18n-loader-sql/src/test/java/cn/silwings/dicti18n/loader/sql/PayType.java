package cn.silwings.dicti18n.loader.sql;

import cn.silwings.dicti18n.dict.Dict;
import lombok.Getter;

@Getter
public enum PayType implements Dict, SwitchDesc {

    ALIPAY("alipay"),
    WECHAT("wechat"),
    ;

    private String code;

    PayType(final String code) {
        this.code = code;
    }


    @Override
    public String dictName() {
        return "pay.pay_type";
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String desc(final String language) {
        switch (language) {
            case "zh-cn": {
                switch (this) {
                    case ALIPAY: {
                        return "支付宝";
                    }
                    case WECHAT: {
                        return "微信";
                    }

                }
            }
            case "en-us": {
                switch (this) {
                    case ALIPAY: {
                        return "支付宝";
                    }
                    case WECHAT: {
                        return "微信";
                    }

                }
            }
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}
