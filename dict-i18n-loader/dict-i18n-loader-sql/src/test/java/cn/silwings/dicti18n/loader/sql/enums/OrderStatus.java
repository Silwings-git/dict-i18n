package cn.silwings.dicti18n.loader.sql.enums;

import cn.silwings.dicti18n.dict.Dict;
import lombok.Getter;

@Getter
public enum OrderStatus implements Dict, SwitchDesc {
    PENDING("pending"),
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");
    private final String code;

    OrderStatus(final String code) {
        this.code = code;
    }

    @Override
    public String dictName() {
        return "order_status";
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
                    case PENDING: {
                        return "待处理";
                    }
                    case PROCESSING: {
                        return "处理中";
                    }
                    case SHIPPED: {
                        return "已发货";
                    }
                    case DELIVERED: {
                        return "已送达";
                    }
                    case CANCELLED: {
                        return "已取消";
                    }
                }
            }
            case "en-us": {
                switch (this) {
                    case PENDING: {
                        return "Pending";
                    }
                    case PROCESSING: {
                        return "Processing";
                    }
                    case SHIPPED: {
                        return "Shipped";
                    }
                    case DELIVERED: {
                        return "Delivered";
                    }
                    case CANCELLED: {
                        return "Cancelled";
                    }
                }
            }
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}
