package cn.silwings.dicti18n.demo.controller;

import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.vo.OrderVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @GetMapping("/order")
    public ResponseEntity<OrderVO> getOrder() {

        final OrderVO orderVO = new OrderVO();
        orderVO.setOrderStatus(OrderStatus.COMPLETED.code());

        return ResponseEntity.ok(orderVO);
    }
}