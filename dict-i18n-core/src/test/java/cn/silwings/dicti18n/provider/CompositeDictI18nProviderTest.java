package cn.silwings.dicti18n.provider;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.sorter.DictLoaderSorter;
import cn.silwings.dicti18n.utils.Maps;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 测试CompositeDictI18nProvider类中的方法
 * Test methods in CompositeDictI18nProvider class
 */
public class CompositeDictI18nProviderTest {

    private static class TestDictLoader implements DictI18nLoader {
        private final Map<String, Map<String, String>> dict;
        private final String name;

        public TestDictLoader(String name, Map<String, Map<String, String>> dict) {
            this.name = name;
            this.dict = dict;
        }

        @Override
        public String loaderName() {
            return this.name;
        }

        @Override
        public Optional<String> get(String lang, String dictKey) {
            return Optional.ofNullable(this.dict.getOrDefault(lang, Collections.emptyMap()).get(dictKey));
        }
    }

    private static class TestDictSorter implements DictLoaderSorter {
        private final List<DictI18nLoader> loaders;

        public TestDictSorter(List<DictI18nLoader> loaders) {
            this.loaders = loaders;
        }

        @Override
        public List<DictI18nLoader> getOrderedLoaders() {
            return this.loaders;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyLoaders() {
        // 测试空加载器构造抛出异常
        // Test constructor throws exception with empty loaders
        new CompositeDictI18nProvider(new TestDictSorter(Collections.emptyList()));
    }

    @Test
    public void testGetTextWithExactMatch() {
        // 测试精确匹配获取文本
        // Test getting text with exact match
        final DictI18nLoader loader1 = new TestDictLoader("loader1",
                Maps.of(
                        "zh-cn", Maps.of("order_status.pending", "待处理"),
                        "en-us", Maps.of("order_status.pending", "Pending")
                ));
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(loader1))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertTrue(result.isPresent());
        assertEquals("待处理", result.get());
    }

    @Test
    public void testGetTextWithFallbackMatch() {
        // 测试降级匹配获取文本
        // Test getting text with fallback match 
        final DictI18nLoader loader1 = new TestDictLoader("loader1",
                Maps.of(
                        "zh", Maps.of("order_status.pending", "待处理")
                ));
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(loader1))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertTrue(result.isPresent());
        assertEquals("待处理", result.get());
    }

    @Test
    public void testGetTextWithDefaultLang() {
        // 测试默认语言匹配获取文本
        // Test getting text with default language match
        final DictI18nLoader loader1 = new TestDictLoader("loader1",
                Maps.of(
                        "en-us", Maps.of("order_status.pending", "Pending")
                ));
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(loader1))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertTrue(result.isPresent());
        assertEquals("Pending", result.get());
    }

    @Test
    public void testGetTextWithFallbackKey() {
        // 测试回退键获取文本
        // Test getting text with fallback key
        final DictI18nLoader loader1 = new TestDictLoader("loader1",
                Maps.of(
                        "", Maps.of("order_status.pending", "Default")
                ));
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(loader1))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertTrue(result.isPresent());
        assertEquals("Default", result.get());
    }

    @Test
    public void testGetTextNotFound() {
        // 测试未找到文本返回空
        // Test returns empty when text not found
        final DictI18nLoader loader1 = new TestDictLoader("loader1", Collections.emptyMap());
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(loader1))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetTextMultipleLoadersPriority() {
        // 测试多加载器的优先级顺序
        // Test multiple loaders priority order
        final DictI18nLoader loader1 = new TestDictLoader("loader1",
                Maps.of(
                        "zh-cn", Maps.of("order_status.pending", "第一加载器")
                ));
        final DictI18nLoader loader2 = new TestDictLoader("loader2",
                Maps.of(
                        "zh-cn", Maps.of("order_status.pending", "第二加载器")
                ));
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Arrays.asList(loader1, loader2))
        );

        final Optional<String> result = provider.getText("zh-CN", "en-US", "order_status", "pending");
        assertTrue(result.isPresent());
        assertEquals("第一加载器", result.get());
    }

    @Test
    public void testResolveKey() {
        // 测试解析key的方法
        // Test resolveKey method
        final CompositeDictI18nProvider provider = new CompositeDictI18nProvider(
                new TestDictSorter(Collections.singletonList(new TestDictLoader("test", Collections.emptyMap())))
        );

        final String key = provider.resolveKey("order_status", "pending");
        assertEquals("order_status.pending", key);
    }
}
