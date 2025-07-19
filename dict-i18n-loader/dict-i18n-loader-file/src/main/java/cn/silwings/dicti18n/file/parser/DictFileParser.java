package cn.silwings.dicti18n.file.parser;

import cn.silwings.dicti18n.file.parser.strategy.DictFileParseStrategy;
import cn.silwings.dicti18n.file.parser.strategy.DictFileParseStrategyRegistry;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * A generic parser class for dictionary files
 * It is implemented through the strategy pattern, which can select the corresponding parsing strategy based on the input resource type for parsing.
 *
 * <p>The responsibility of this class is to interact with {@link DictFileParseStrategyRegistry},
 * retrieve the appropriate parsing strategy from the registry, and invoke its parsing method to process the dictionary file.</p>
 */
public class DictFileParser {

    private final DictFileParseStrategyRegistry registry;

    public DictFileParser(final DictFileParseStrategyRegistry registry) {
        this.registry = registry;
    }

    /**
     * parse dictionary files
     *
     * @param resource Resources to parse
     * @return Parsed dictionary information list
     */
    public List<DictInfo> parse(final Resource resource) {
        final DictFileParseStrategy strategy = registry.getStrategy(resource);
        return strategy.parse(resource);
    }

}
