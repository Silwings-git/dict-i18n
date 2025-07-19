package cn.silwings.dicti18n.file.config;

import cn.silwings.dicti18n.file.FileDictI18nLoader;
import cn.silwings.dicti18n.file.parser.DictFileParser;
import cn.silwings.dicti18n.file.parser.strategy.DictFileParseStrategy;
import cn.silwings.dicti18n.file.parser.strategy.DictFileParseStrategyRegistry;
import cn.silwings.dicti18n.file.parser.strategy.PropertiesDictParseStrategy;
import cn.silwings.dicti18n.file.parser.strategy.YmlDictParseStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "dict-i18n.loader.file", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FileDictI18nLoaderAutoConfiguration {

    @Bean
    public FileDictI18nLoaderProperties fileDictI18nLoaderProperties() {
        return new FileDictI18nLoaderProperties();
    }

    @Bean
    public YmlDictParseStrategy ymlDictParseStrategy() {
        return new YmlDictParseStrategy();
    }

    @Bean
    public PropertiesDictParseStrategy propertiesDictParseStrategy() {
        return new PropertiesDictParseStrategy();
    }

    @Bean
    public DictFileParseStrategyRegistry dictFileParseStrategyRegistry(final List<DictFileParseStrategy> strategies) {
        return new DictFileParseStrategyRegistry(strategies);
    }

    @Bean
    public DictFileParser dictFileParser(final DictFileParseStrategyRegistry registry) {
        return new DictFileParser(registry);
    }

    @Bean
    public FileDictI18nLoader fileDictI18nLoader(final FileDictI18nLoaderProperties fileDictI18nLoaderProperties, final DictFileParser dictFileParser) {
        return new FileDictI18nLoader(fileDictI18nLoaderProperties, dictFileParser);
    }
}
