package cn.silwings.dicti18n.loader.file.config;

import cn.silwings.dicti18n.loader.file.FileDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictFileParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FileDictI18nLoaderProperties.class)
@ConditionalOnProperty(prefix = "dict-i18n.loader.file", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FileDictI18nLoaderAutoConfiguration {
    @Bean
    public FileDictI18nLoader fileDictI18nLoader(final FileDictI18nLoaderProperties fileDictI18nLoaderProperties, final DictFileParser dictFileParser) {
        return new FileDictI18nLoader(fileDictI18nLoaderProperties, dictFileParser);
    }
}
