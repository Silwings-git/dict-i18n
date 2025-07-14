package cn.silwings.dicti18n.loader.properties.config;

import cn.silwings.dicti18n.loader.properties.impl.PropertiesDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(PropertiesDictI18nLoader.class)
public class PropertiesDictI18nLoaderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.loader.properties")
    public DictI18nPropsProperties dictI18nPropsProperties() {
        return new DictI18nPropsProperties();
    }

    @Bean
    public PropertiesDictI18nLoader propertiesDictI18nLoader(final DictI18nPropsProperties dictI18nPropsProperties) {
        return new PropertiesDictI18nLoader(dictI18nPropsProperties);
    }

}