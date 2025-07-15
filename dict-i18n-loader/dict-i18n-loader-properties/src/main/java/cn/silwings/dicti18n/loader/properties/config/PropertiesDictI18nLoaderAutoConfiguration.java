package cn.silwings.dicti18n.loader.properties.config;

import cn.silwings.dicti18n.loader.properties.impl.PropertiesDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesDictI18nLoaderAutoConfiguration {

    @Bean
    public PropsDictI18nLoaderProperties propsDictI18nLoaderProperties() {
        return new PropsDictI18nLoaderProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.loader.properties", name = "enabled", havingValue = "true", matchIfMissing = true)
    public PropertiesDictI18nLoader propertiesDictI18nLoader(final PropsDictI18nLoaderProperties propsDictI18nLoaderProperties) {
        return new PropertiesDictI18nLoader(propsDictI18nLoaderProperties);
    }

}