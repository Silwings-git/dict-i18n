package cn.silwings.dicti18n.loader.parser.strategy;

import org.springframework.core.io.Resource;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DictFileParseStrategyRegistry {

    private final List<DictFileParseStrategy> strategies;

    public DictFileParseStrategyRegistry() {
        this.strategies = new CopyOnWriteArrayList<>();
    }

    public DictFileParseStrategyRegistry(final List<DictFileParseStrategy> strategies) {
        this.strategies = new CopyOnWriteArrayList<>(strategies);
    }

    public void register(final DictFileParseStrategy strategy) {
        if (null == strategy) {
            throw new IllegalArgumentException("Strategy can not be null");
        }
        this.strategies.add(strategy);
    }

    public DictFileParseStrategy getStrategy(final Resource resource) {
        return this.strategies
                .stream()
                .filter(s -> s.supports(resource))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("[DictI18n] Unsupported file format: " + resource.getFilename()));
    }
}
