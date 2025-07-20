package cn.silwings.dicti18n.loader.config;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategy;
import cn.silwings.dicti18n.loader.parser.strategy.DictFileParseStrategyRegistry;
import cn.silwings.dicti18n.loader.parser.strategy.PropertiesDictParseStrategy;
import cn.silwings.dicti18n.loader.parser.strategy.YmlDictParseStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoreDictI18nLoaderAutoConfiguration {

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

}
