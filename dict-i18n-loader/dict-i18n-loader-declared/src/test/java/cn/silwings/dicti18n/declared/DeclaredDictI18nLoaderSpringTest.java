package cn.silwings.dicti18n.declared;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(properties = {
        "dicti18n.declared.scan-packages=cn.silwings.dicti18n.declared"
})
class DeclaredDictI18nLoaderSpringTest {

    @Autowired
    private DictI18nLoader dictI18nLoader;

    @Test
    void testLoaderInitializationWithSpringContext() {
        // 测试加载器是否正确初始化
        assertNotNull(this.dictI18nLoader);
        assertEquals("declared", this.dictI18nLoader.loaderName());

        // 测试可以获取到测试枚举字典的值
        final Optional<String> result = this.dictI18nLoader.get(null, "TEST_SPRING.ENUM1");
        assertTrue(result.isPresent());
        assertEquals(TestSpringDict.ENUM1.getDesc(), result.get());
    }

    @Test
    void testCustomDictImplementation() {
        // 测试自定义字典实现
        final Optional<String> result = dictI18nLoader.get(null, "CUSTOM.KEY");
        assertTrue(result.isPresent());
        assertEquals(new CustomDict().getDesc(), result.get());
    }

    @Test
    void testNonExistentDictKey() {
        // 测试不存在的键
        final Optional<String> result = dictI18nLoader.get(null, "NON.EXISTENT");
        assertFalse(result.isPresent());
    }

    @Test
    void testDifferentLanguages() {
        // 测试不同的语言
        final Optional<String> result = dictI18nLoader.get(null, "CUSTOM.KEY");
        assertTrue(result.isPresent());
        final Optional<String> resultCN = dictI18nLoader.get("cn", "CUSTOM.KEY");
        assertTrue(resultCN.isPresent());
        assertEquals(result.get(), resultCN.get());
    }

    @Test
    void testUndeclaredDictWithDesc() {
        final Optional<String> result = dictI18nLoader.get(null, "undeclared_with_desc.OK");
        assertTrue(result.isPresent());
        assertEquals(UndeclaredDictWithDesc.STATUS_OK.getDescription(), result.get());
    }

    @Test
    void testUndeclaredDict() {
        final Optional<String> result = dictI18nLoader.get(null, "undeclared.OK");
        assertTrue(result.isPresent());
        assertEquals(UndeclaredDict.STATUS_OK.name(), result.get());
    }

}