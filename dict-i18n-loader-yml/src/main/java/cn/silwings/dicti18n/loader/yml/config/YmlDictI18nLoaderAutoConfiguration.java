package cn.silwings.dicti18n.loader.yml.config;

import cn.silwings.dicti18n.loader.yml.impl.YmlDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(YmlDictI18nLoader.class)
public class YmlDictI18nLoaderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.loader.yml")
    public DictI18nYmlProperties dictI18nYmlProperties() {
        return new DictI18nYmlProperties();
    }

    @Bean
    public YmlDictI18nLoader ymlDictI18nLoader(final DictI18nYmlProperties dictI18nYmlProperties) {
        return new YmlDictI18nLoader(dictI18nYmlProperties);
    }

}