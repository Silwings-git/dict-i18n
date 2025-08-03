package cn.silwings.dicti18n.demo;

import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.dict.PaymentType;
import cn.silwings.dicti18n.demo.dict.ShippingStatus;
import cn.silwings.dicti18n.demo.vo.OrderVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;


@AutoConfigureMockMvc
@SpringBootTest(classes = DemoApplication.class, properties = {
        "dict-i18n.loader.file.enabled=false",
        "dict-i18n.loader.declared.enabled=true",
        "dict-i18n.loader.redis.enabled=false",
        "dict-i18n.loader.sql.enabled=false",
})
public class DeclaredBasicUsageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getZHCNOrderShouldWork() throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final OrderVO body = this.objectMapper.readValue(responseString, OrderVO.class);

        Assertions.assertNotNull(body);
        Assertions.assertEquals(OrderStatus.PENDING.code(), body.getOrderStatus());
        // OrderStatus is an enum type;
        Assertions.assertEquals("PENDING", body.getOrderStatusDesc());
        Assertions.assertEquals("PENDING", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("SHIPPED", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        Assertions.assertEquals("支付宝", body.getPaymentTypeDesc());
    }

    @Test
    public void getZHOrderShouldWork() throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final OrderVO body = this.objectMapper.readValue(responseString, OrderVO.class);

        Assertions.assertNotNull(body);
        Assertions.assertEquals(OrderStatus.PENDING.code(), body.getOrderStatus());
        // OrderStatus is an enum type;
        Assertions.assertEquals("PENDING", body.getOrderStatusDesc());
        Assertions.assertEquals("PENDING", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("SHIPPED", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader Ignore language
        Assertions.assertEquals("支付宝", body.getPaymentTypeDesc());
    }

    @Test
    public void getENUSOrderShouldWork() throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final OrderVO body = this.objectMapper.readValue(responseString, OrderVO.class);

        Assertions.assertNotNull(body);
        Assertions.assertEquals(OrderStatus.PENDING.code(), body.getOrderStatus());
        // OrderStatus is an enum type;
        Assertions.assertEquals("PENDING", body.getOrderStatusDesc());
        Assertions.assertEquals("PENDING", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("SHIPPED", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("支付宝", body.getPaymentTypeDesc());
    }

    @Test
    public void getENOrderShouldWork() throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final OrderVO body = this.objectMapper.readValue(responseString, OrderVO.class);

        Assertions.assertNotNull(body);
        Assertions.assertEquals(OrderStatus.PENDING.code(), body.getOrderStatus());
        // OrderStatus is an enum type;
        Assertions.assertEquals("PENDING", body.getOrderStatusDesc());
        Assertions.assertEquals("PENDING", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("SHIPPED", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("支付宝", body.getPaymentTypeDesc());
    }

}
