package cn.silwings.dicti18n.demo.vo;


import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.dict.PaymentType;
import cn.silwings.dicti18n.demo.dict.ShippingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderVO {

    private String orderStatus;

    @DictDesc(OrderStatus.class)
    private String orderStatusDesc;

    @DictDesc(value = OrderStatus.class, field = "orderStatus")
    private String status;

    private ShippingStatus shippingStatus;

    @DictDesc(ShippingStatus.class)
    private String shippingStatusDesc;

    private String paymentType;

    @DictDesc(PaymentType.class)
    private String paymentTypeDesc;

}