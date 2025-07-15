package cn.silwings.dicti18n.loader.yml.config;

import cn.silwings.dicti18n.loader.yml.impl.YmlDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YmlDictI18nLoaderAutoConfiguration {

    @Bean
    public YmlDictI18nLoaderProperties ymlDictI18nLoaderProperties() {
        return new YmlDictI18nLoaderProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.loader.yml", name = "enabled", havingValue = "true", matchIfMissing = true)
    public YmlDictI18nLoader ymlDictI18nLoader(final YmlDictI18nLoaderProperties ymlDictI18nLoaderProperties) {
        return new YmlDictI18nLoader(ymlDictI18nLoaderProperties);
    }

}