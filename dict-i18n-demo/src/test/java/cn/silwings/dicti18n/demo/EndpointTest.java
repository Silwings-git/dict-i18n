package cn.silwings.dicti18n.demo;

import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.dict.PaymentType;
import cn.silwings.dicti18n.demo.dict.ShippingStatus;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemVO;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoApplication.class)
public class EndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void dictNamesShouldWork() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dict-i18n/dict-names")
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final List<String> dictNames = this.objectMapper.readValue(responseString, new TypeReference<List<String>>() {
        });

        Assertions.assertNotNull(dictNames);
        Assertions.assertEquals(3, dictNames.size());
        Assertions.assertTrue(dictNames.contains(OrderStatus.PENDING.dictName()));
        Assertions.assertTrue(dictNames.contains(PaymentType.WECHAT.dictName()));
        Assertions.assertTrue(dictNames.contains(ShippingStatus.PENDING.dictName()));
    }

    @Test
    public void zhDictItemsShouldWork() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dict-i18n/dict-items")
                .param("dictNames", "order_status,payment_type,shipping_status")
                .param("language", "zh")
                .characterEncoding(StandardCharsets.UTF_8);

        this.matchZh(requestBuilder);
    }

    @Test
    public void enUSDictItemsShouldWork() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dict-i18n/dict-items")
                .param("dictNames", "order_status,payment_type,shipping_status")
                .param("language", "en-US")
                .characterEncoding(StandardCharsets.UTF_8);

        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final DictItemsResponse dictItems = this.objectMapper.readValue(responseString, new TypeReference<DictItemsResponse>() {
        });

        Assertions.assertNotNull(dictItems);
        Assertions.assertEquals(3, dictItems.getItems().size());
        Assertions.assertTrue(dictItems.getItems().containsKey(OrderStatus.PENDING.dictName()));
        Assertions.assertTrue(dictItems.getItems().containsKey(PaymentType.WECHAT.dictName()));
        Assertions.assertTrue(dictItems.getItems().containsKey(ShippingStatus.PENDING.dictName()));

        final Map<String, String> orderStatusItemMap = dictItems.getItems().get(OrderStatus.PENDING.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(OrderStatus.values().length, orderStatusItemMap.size());
        Assertions.assertEquals("Canceled", orderStatusItemMap.get(OrderStatus.CANCELED.code()));
        Assertions.assertEquals("Completed", orderStatusItemMap.get(OrderStatus.COMPLETED.code()));
        Assertions.assertEquals("Paid", orderStatusItemMap.get(OrderStatus.PAID.code()));
        Assertions.assertEquals("Pending", orderStatusItemMap.get(OrderStatus.PENDING.code()));
        Assertions.assertEquals("Shipped", orderStatusItemMap.get(OrderStatus.SHIPPED.code()));

        final Map<String, String> paymentTypeItemMap = dictItems.getItems().get(PaymentType.WECHAT.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(PaymentType.values().length, paymentTypeItemMap.size());
        Assertions.assertEquals("微信", paymentTypeItemMap.get(PaymentType.WECHAT.code()));
        Assertions.assertEquals("支付宝", paymentTypeItemMap.get(PaymentType.ALIPAY.code()));

        final Map<String, String> shippingStatusItemMap = dictItems.getItems().get(ShippingStatus.SHIPPED.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(ShippingStatus.values().length, shippingStatusItemMap.size());
        Assertions.assertEquals("Delivered", shippingStatusItemMap.get(ShippingStatus.DELIVERED.code()));
        Assertions.assertEquals("Delivery-Failed", shippingStatusItemMap.get(ShippingStatus.DELIVERY_FAILED.code()));
        Assertions.assertEquals("In-Transit", shippingStatusItemMap.get(ShippingStatus.IN_TRANSIT.code()));
        Assertions.assertEquals("Pending", shippingStatusItemMap.get(ShippingStatus.PENDING.code()));
        Assertions.assertEquals("Returned", shippingStatusItemMap.get(ShippingStatus.RETURNED.code()));
        Assertions.assertEquals("Returning", shippingStatusItemMap.get(ShippingStatus.RETURNING.code()));
        Assertions.assertEquals("Shipped", shippingStatusItemMap.get(ShippingStatus.SHIPPED.code()));
    }

    /**
     * Although the request header specified en, the default (zh) configuration was used due to the absence of the dict_en.yml configuration
     */
    @Test
    public void enDictItemsShouldWork() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dict-i18n/dict-items")
                .param("dictNames", "order_status,payment_type,shipping_status")
                .param("language", "en")
                .characterEncoding(StandardCharsets.UTF_8);

        this.matchZh(requestBuilder);
    }

    private void matchZh(final MockHttpServletRequestBuilder requestBuilder) throws Exception {
        final String responseString = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final DictItemsResponse dictItems = this.objectMapper.readValue(responseString, new TypeReference<DictItemsResponse>() {
        });

        Assertions.assertNotNull(dictItems);
        Assertions.assertEquals(3, dictItems.getItems().size());
        Assertions.assertTrue(dictItems.getItems().containsKey(OrderStatus.PENDING.dictName()));
        Assertions.assertTrue(dictItems.getItems().containsKey(PaymentType.WECHAT.dictName()));
        Assertions.assertTrue(dictItems.getItems().containsKey(ShippingStatus.PENDING.dictName()));

        final Map<String, String> orderStatusItemMap = dictItems.getItems().get(OrderStatus.PENDING.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(OrderStatus.values().length, orderStatusItemMap.size());
        Assertions.assertEquals("已取消", orderStatusItemMap.get(OrderStatus.CANCELED.code()));
        Assertions.assertEquals("已完成", orderStatusItemMap.get(OrderStatus.COMPLETED.code()));
        Assertions.assertEquals("已支付", orderStatusItemMap.get(OrderStatus.PAID.code()));
        Assertions.assertEquals("待处理", orderStatusItemMap.get(OrderStatus.PENDING.code()));
        Assertions.assertEquals("已发货", orderStatusItemMap.get(OrderStatus.SHIPPED.code()));

        final Map<String, String> paymentTypeItemMap = dictItems.getItems().get(PaymentType.WECHAT.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(PaymentType.values().length, paymentTypeItemMap.size());
        Assertions.assertEquals("微信", paymentTypeItemMap.get(PaymentType.WECHAT.code()));
        Assertions.assertEquals("支付宝", paymentTypeItemMap.get(PaymentType.ALIPAY.code()));

        final Map<String, String> shippingStatusItemMap = dictItems.getItems().get(ShippingStatus.SHIPPED.dictName()).stream().collect(Collectors.toMap(DictItemVO::getCode, DictItemVO::getDesc));
        Assertions.assertEquals(ShippingStatus.values().length, shippingStatusItemMap.size());
        Assertions.assertEquals("已签收", shippingStatusItemMap.get(ShippingStatus.DELIVERED.code()));
        Assertions.assertEquals("签收失败", shippingStatusItemMap.get(ShippingStatus.DELIVERY_FAILED.code()));
        Assertions.assertEquals("运输中", shippingStatusItemMap.get(ShippingStatus.IN_TRANSIT.code()));
        Assertions.assertEquals("待发货", shippingStatusItemMap.get(ShippingStatus.PENDING.code()));
        Assertions.assertEquals("已退货", shippingStatusItemMap.get(ShippingStatus.RETURNED.code()));
        Assertions.assertEquals("退货中", shippingStatusItemMap.get(ShippingStatus.RETURNING.code()));
        Assertions.assertEquals("已发货", shippingStatusItemMap.get(ShippingStatus.SHIPPED.code()));
    }
}
