package cn.silwings.dicti18n.demo.vo;


import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.demo.dict.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderVO {

    private String orderStatus;

    @DictDesc(OrderStatus.class)
    private String orderStatusDesc;

}