package cn.silwings.dicti18n.demo.controller;

import cn.silwings.dicti18n.demo.dict.OrderCountry;
import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.vo.OrderVO;
import cn.silwings.dicti18n.demo.vo.ProductVO;
import cn.silwings.dicti18n.starter.annotation.DisableDictI18n;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/orders")
    public ResponseEntity<List<OrderVO>> getOrders() {

        final List<OrderVO> orderList = new ArrayList<>();
        orderList.add(new OrderVO()
                .setOrderNo("123456")
                .setOrderStatus(OrderStatus.CREATED.code())
                .setCountryCode(OrderCountry.CN.code())
        );

        return ResponseEntity.ok(orderList);
    }

    @DisableDictI18n
    @GetMapping("/products")
    public ResponseEntity<Map<String, ProductVO>> getProducts() {

        final ProductVO product = new ProductVO().setCountry(OrderCountry.CN.code());

        final Map<String, ProductVO> resultMap = new HashMap<>();
        resultMap.put("PC001", product);

        return ResponseEntity.ok(resultMap);
    }

}