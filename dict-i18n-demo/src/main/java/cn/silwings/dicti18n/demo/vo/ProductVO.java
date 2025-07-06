package cn.silwings.dicti18n.demo.vo;

import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.demo.dict.OrderCountry;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProductVO {

    private String country;

    @DictDesc(OrderCountry.class)
    private String countryDesc;

}