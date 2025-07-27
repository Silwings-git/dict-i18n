package cn.silwings.dicti18n.loader.sql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApp.class, properties = "dict-i18n.loader.sql.cache.enabled=true")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class SqlDictI18nLoaderCacheTest {

    @Autowired
    private SqlDictI18nLoader loader;

    @Test
    void testLoadDictEnUs() {
        Optional<String> result = loader.get("en-us", "order_status.pending");
        assertTrue(result.isPresent());
        assertEquals("Pending", result.get());

        result = loader.get("en-us", "order_status.processing");
        assertTrue(result.isPresent());
        assertEquals("Processing", result.get());

        result = loader.get("en-us", "order_status.shipped");
        assertTrue(result.isPresent());
        assertEquals("Shipped", result.get());

        result = loader.get("en-us", "order_status.delivered");
        assertTrue(result.isPresent());
        assertEquals("Delivered", result.get());

        result = loader.get("en-us", "order_status.cancelled");
        assertTrue(result.isPresent());
        assertEquals("Cancelled", result.get());

        result = loader.get("en-us", "pay.pay_type.alipay");
        assertTrue(result.isPresent());
        assertEquals("Alipay", result.get());

        result = loader.get("en-us", "pay.pay_type.wechat");
        assertTrue(result.isPresent());
        assertEquals("WeChat Pay", result.get());
    }

    @Test
    void testLoadDictZhCn() {
        Optional<String> result = loader.get("zh-cn", "order_status.pending");
        assertTrue(result.isPresent());
        assertEquals("待处理", result.get());

        result = loader.get("zh-cn", "order_status.processing");
        assertTrue(result.isPresent());
        assertEquals("处理中", result.get());

        result = loader.get("zh-cn", "order_status.shipped");
        assertTrue(result.isPresent());
        assertEquals("已发货", result.get());

        result = loader.get("zh-cn", "order_status.delivered");
        assertTrue(result.isPresent());
        assertEquals("已送达", result.get());

        result = loader.get("zh-cn", "order_status.cancelled");
        assertTrue(result.isPresent());
        assertEquals("已取消", result.get());

        result = loader.get("zh-cn", "pay.pay_type.alipay");
        assertTrue(result.isPresent());
        assertEquals("支付宝", result.get());

        result = loader.get("zh-cn", "pay.pay_type.wechat");
        assertTrue(result.isPresent());
        assertEquals("微信支付", result.get());
    }

    @Test
    void testNonExistentKey() {
        // 测试不存在的键
        final Optional<String> result = loader.get("en", "non.existent");
        assertFalse(result.isPresent());
    }

    @Test
    void testNonExistentLanguage() {
        // 测试不存在的语言
        Optional<String> result = loader.get("fr", "test.key1");
        assertFalse(result.isPresent());

        result = loader.get("en_us", "pay.pay_type.alipay");
        assertFalse(result.isPresent());

        result = loader.get("en-US", "pay.pay_type.alipay");
        assertFalse(result.isPresent());
    }

}
