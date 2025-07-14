package cn.silwings.dicti18n.config;

import cn.silwings.dicti18n.impl.StaticDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(DictI18nStaticProperties.class)
public class StaticDictI18nLoaderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.loader.static")
    public DictI18nStaticProperties dictI18nStaticProperties() {
        return new DictI18nStaticProperties();
    }

    @Bean
    public StaticDictI18nLoader ymlDictI18nLoader(final DictI18nStaticProperties dictI18nStaticProperties) {
        return new StaticDictI18nLoader(dictI18nStaticProperties);
    }

}