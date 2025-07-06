package cn.silwings.dicti18n.demo.vo;


import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.demo.dict.OrderCountry;
import cn.silwings.dicti18n.demo.dict.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderVO {

    private String orderNo;

    private String orderStatus;

    @DictDesc(OrderStatus.class)
    private String orderStatusDesc;

    private String countryCode;

    @DictDesc(value = OrderCountry.class, field = "countryCode")
    private String countryName;

}