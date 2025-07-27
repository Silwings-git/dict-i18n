package cn.silwings.dicti18n.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApp.class)
class FileDictI18nLoaderSpringTest {

    @Autowired
    private FileDictI18nLoader loader;

    @Test
    void testLoadDictFromFile() {
        final Optional<String> resultEN = loader.get("en-us", "order_status.pending");
        assertTrue(resultEN.isPresent());
        assertEquals("Pending", resultEN.get());

        final Optional<String> resultCN = loader.get("zh-cn", "order_status.pending");
        assertTrue(resultCN.isPresent());
        assertEquals("待处理", resultCN.get());
    }

    @Test
    void testLoadDictFromFile2() {
        final Optional<String> resultCN = loader.get("zh-cn", "pay.pay_type.alipay");
        assertTrue(resultCN.isPresent());
        assertEquals("支付宝", resultCN.get());

        final Optional<String> resultZHCN = loader.get("en-us", "pay.pay_type.alipay");
        assertTrue(resultZHCN.isPresent());
        assertEquals("Alipay", resultZHCN.get());
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