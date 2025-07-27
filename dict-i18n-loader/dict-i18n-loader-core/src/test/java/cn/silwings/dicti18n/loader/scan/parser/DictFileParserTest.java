package cn.silwings.dicti18n.loader.scan.parser;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategy;
import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictFileParserTest {

    @Mock
    private DictFileParseStrategyRegistry mockRegistry;
    @Mock
    private DictFileParseStrategy mockStrategy;
    @Mock
    private Resource mockResource;
    @InjectMocks
    private DictFileParser parser;

    @Test
    void parseShouldDelegateToStrategy() {
        // 验证解析逻辑是否委托给正确的策略
        final List<DictInfo> expected = Collections.singletonList(new DictInfo("test.key", "test.desc"));
        when(mockRegistry.getStrategy(mockResource)).thenReturn(mockStrategy);
        when(mockStrategy.parse(mockResource)).thenReturn(expected);

        final List<DictInfo> result = parser.parse(mockResource);
        assertSame(expected, result);
        verify(mockRegistry).getStrategy(mockResource);
        verify(mockStrategy).parse(mockResource);
    }
}