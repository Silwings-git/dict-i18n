package cn.silwings.dicti18n.demo;

import cn.silwings.dicti18n.demo.dict.OrderStatus;
import cn.silwings.dicti18n.demo.dict.PaymentType;
import cn.silwings.dicti18n.demo.dict.ShippingStatus;
import cn.silwings.dicti18n.demo.vo.OrderVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
import java.nio.file.Path;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoApplication.class, properties = {
        "dict-i18n.loader.file.enabled=false",
        "dict-i18n.loader.declared.enabled=false",
        "dict-i18n.loader.redis.enabled=false",
        "dict-i18n.loader.sql.enabled=true",
        "dict-i18n.loader.sql.schema.enabled=true",
        "dict-i18n.loader.sql.preload.enabled=true",
        "dict-i18n.loader.sql.preload.preload-mode=full"
})
public class SqliteBasicUsageTest {

    @TempDir
    static Path tempDir;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        final String dbPath = tempDir.resolve("dict_i18n_test.db").toString();
        System.setProperty("spring.datasource.url", "jdbc:sqlite:" + dbPath);
    }

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
        Assertions.assertEquals("待处理", body.getOrderStatusDesc());
        Assertions.assertEquals("待处理", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("已发货", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("payment_type.ALIPAY", body.getPaymentTypeDesc());
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
        Assertions.assertEquals("待处理", body.getOrderStatusDesc());
        Assertions.assertEquals("待处理", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("已发货", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("payment_type.ALIPAY", body.getPaymentTypeDesc());
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
        Assertions.assertEquals("Pending", body.getOrderStatusDesc());
        Assertions.assertEquals("Pending", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("Shipped", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("payment_type.ALIPAY", body.getPaymentTypeDesc());
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

        // Although the request header specified en, the default (zh) configuration was used due to the absence of the dict_en.yml configuration
        Assertions.assertNotNull(body);
        Assertions.assertEquals(OrderStatus.PENDING.code(), body.getOrderStatus());
        Assertions.assertEquals("待处理", body.getOrderStatusDesc());
        Assertions.assertEquals("待处理", body.getStatus());
        Assertions.assertEquals(ShippingStatus.SHIPPED, body.getShippingStatus());
        Assertions.assertEquals("已发货", body.getShippingStatusDesc());
        Assertions.assertEquals(PaymentType.ALIPAY.code(), body.getPaymentType());
        // declared loader is not enabled
        Assertions.assertEquals("payment_type.ALIPAY", body.getPaymentTypeDesc());
    }

}
