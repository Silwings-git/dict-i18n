package cn.silwings.dicti18n.loader.scan.parser;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.parser.strategy.YmlDictParseStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YmlDictParseStrategyTest {

    private final YmlDictParseStrategy strategy = new YmlDictParseStrategy();

    @Mock
    private Resource mockResource;

    @Test
    void supportsShouldReturnTrueForYmlFile() {
        // 支持.yml文件
        when(mockResource.getFilename()).thenReturn("test.yml");
        assertTrue(strategy.supports(mockResource));
    }

    @Test
    void supportsShouldReturnFalseForNonYmlFile() {
        // 不支持非.yml文件
        when(mockResource.getFilename()).thenReturn("test.properties");
        assertFalse(strategy.supports(mockResource));
    }

    @Test
    void parseShouldFlattenNestedYmlAndReturnDictInfo() throws IOException {
        // 解析嵌套YML并扁平化键
        final String ymlContent = "orderStatus:\n  pending: 待处理\n  shipped: 已发货\nuser:\n  status:\n    active: 激活";
        final InputStream is = new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8));
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(is);

        final List<DictInfo> result = strategy.parse(mockResource);

        assertEquals(3, result.size());
        // 验证扁平化后的键
        assertTrue(result.stream().anyMatch(d -> d.getDictKey().equals("orderStatus.pending") && d.getDictDesc().equals("待处理")));
        assertTrue(result.stream().anyMatch(d -> d.getDictKey().equals("orderStatus.shipped") && d.getDictDesc().equals("已发货")));
        assertTrue(result.stream().anyMatch(d -> d.getDictKey().equals("user.status.active") && d.getDictDesc().equals("激活")));
    }

    @Test
    void parseShouldReturnEmptyListWhenResourceNotExists() {
        // 资源不存在时返回空列表
        when(mockResource.exists()).thenReturn(false);
        final List<DictInfo> result = strategy.parse(mockResource);
        assertTrue(result.isEmpty());
    }

    @Test
    void parseShouldReturnEmptyListWhenReadFails() throws IOException {
        // 读取异常时返回空列表
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenThrow(IOException.class);

        final List<DictInfo> result = strategy.parse(mockResource);
        assertTrue(result.isEmpty());
    }

    @Test
    void parseShouldIgnoreNullValues() throws IOException {
        // 忽略null值
        final String ymlContent = "a: null\nb: 有效值";
        final InputStream is = new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8));
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(is);

        final List<DictInfo> result = strategy.parse(mockResource);
        assertEquals(1, result.size());
        assertEquals("b", result.get(0).getDictKey());
    }
}