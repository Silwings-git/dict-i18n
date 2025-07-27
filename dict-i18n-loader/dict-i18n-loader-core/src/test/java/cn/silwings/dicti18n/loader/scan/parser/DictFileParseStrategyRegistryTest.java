package cn.silwings.dicti18n.loader.scan.parser;

import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategy;
import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictFileParseStrategyRegistryTest {

    @Mock
    private DictFileParseStrategy mockStrategy1;
    @Mock
    private DictFileParseStrategy mockStrategy2;
    @Mock
    private Resource mockResource;

    @Test
    void registerShouldThrowExceptionWhenStrategyIsNull() {
        // 注册null策略应抛出异常
        final DictFileParseStrategyRegistry registry = new DictFileParseStrategyRegistry();
        assertThrows(IllegalArgumentException.class, () -> registry.register(null));
    }

    @Test
    void getStrategyShouldReturnMatchedStrategy() {
        // 存在匹配的策略时返回对应策略
        when(mockStrategy2.supports(mockResource)).thenReturn(true);

        final DictFileParseStrategyRegistry registry = new DictFileParseStrategyRegistry(
                Collections.singletonList(mockStrategy2)
        );
        final DictFileParseStrategy result = registry.getStrategy(mockResource);
        assertSame(mockStrategy2, result);
    }

    @Test
    void getStrategyShouldThrowExceptionWhenNoMatchedStrategy() {
        // 无匹配策略时抛出异常
        when(mockStrategy1.supports(mockResource)).thenReturn(false);
        final DictFileParseStrategyRegistry registry = new DictFileParseStrategyRegistry(
                Collections.singletonList(mockStrategy1)
        );

        assertThrows(UnsupportedOperationException.class, () -> registry.getStrategy(mockResource));
    }
}