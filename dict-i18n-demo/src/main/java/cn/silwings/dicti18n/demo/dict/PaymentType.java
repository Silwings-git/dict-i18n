package cn.silwings.dicti18n.demo.dict;

import cn.silwings.dicti18n.loader.declared.dict.DeclaredDict;

public enum PaymentType implements DeclaredDict {

    WECHAT {
        @Override
        public String getDesc() {
            return "微信";
        }
    },
    ALIPAY {
        @Override
        public String getDesc() {
            return "支付宝";
        }
    };

    @Override
    public String dictName() {
        return "payment_type";
    }

    @Override
    public String code() {
        return this.name();
    }
}
