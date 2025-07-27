package cn.silwings.dicti18n.loader.scan.parser;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.parser.strategy.PropertiesDictParseStrategy;
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
class PropertiesDictParseStrategyTest {

    private final PropertiesDictParseStrategy strategy = new PropertiesDictParseStrategy();

    @Mock
    private Resource mockResource;

    @Test
    void supportsShouldReturnTrueForPropertiesFile() {
        // 支持.properties文件
        when(mockResource.getFilename()).thenReturn("test.properties");
        assertTrue(strategy.supports(mockResource));
    }

    @Test
    void supportsShouldReturnFalseForNonPropertiesFile() {
        // 不支持非.properties文件
        when(mockResource.getFilename()).thenReturn("test.yml");
        assertFalse(strategy.supports(mockResource));
    }

    @Test
    void parseShouldReturnDictInfoListForValidProperties() throws IOException {
        // 解析有效的properties文件
        final String content = "orderStatus.pending=待处理\norderStatus.shipped=已发货";
        final InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(is);

        final List<DictInfo> result = strategy.parse(mockResource);

        assertEquals(2, result.size());
        assertEquals("orderStatus.pending", result.get(0).getDictKey());
        assertEquals("待处理", result.get(0).getDictDesc());
        assertEquals("orderStatus.shipped", result.get(1).getDictKey());
        assertEquals("已发货", result.get(1).getDictDesc());
    }

    @Test
    void parseShouldReturnEmptyListWhenResourceNotExists() throws IOException {
        // 资源不存在时返回空列表
        when(mockResource.exists()).thenReturn(false);

        final List<DictInfo> result = strategy.parse(mockResource);
        assertTrue(result.isEmpty());
    }

    @Test
    void parseShouldReturnEmptyListWhenReadFails() throws IOException {
        // 读取文件异常时返回空列表
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenThrow(IOException.class);

        List<DictInfo> result = strategy.parse(mockResource);
        assertTrue(result.isEmpty());
    }
}