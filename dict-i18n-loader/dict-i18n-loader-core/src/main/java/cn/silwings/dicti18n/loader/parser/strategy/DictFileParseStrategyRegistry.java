package cn.silwings.dicti18n.loader.parser.strategy;

import org.springframework.core.io.Resource;

import java.util.List;

public class DictFileParseStrategyRegistry {

    private final List<DictFileParseStrategy> strategies;

    public DictFileParseStrategyRegistry(final List<DictFileParseStrategy> strategies) {
        this.strategies = strategies;
    }

    public DictFileParseStrategy getStrategy(final Resource resource) {
        return this.strategies
                .stream()
                .filter(s -> s.supports(resource))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("[DictI18n] Unsupported file format: " + resource.getFilename()));
    }
}
