package cn.silwings.dicti18n.demo.controller;

import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.dict.PaymentType;
import cn.silwings.dicti18n.demo.dict.ShippingStatus;
import cn.silwings.dicti18n.demo.vo.OrderVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @GetMapping("/order")
    public ResponseEntity<OrderVO> getOrder() {

        final OrderVO orderVO = new OrderVO();
        orderVO.setOrderStatus(OrderStatus.PENDING.code());
        orderVO.setShippingStatus(ShippingStatus.SHIPPED);
        orderVO.setPaymentType(PaymentType.ALIPAY.code());

        return ResponseEntity.ok(orderVO);
    }
}