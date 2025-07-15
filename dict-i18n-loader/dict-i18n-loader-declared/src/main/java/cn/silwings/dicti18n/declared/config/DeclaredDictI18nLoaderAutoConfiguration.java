package cn.silwings.dicti18n.declared.config;

import cn.silwings.dicti18n.declared.impl.DeclaredDictLoader;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeclaredDictI18nLoaderAutoConfiguration {

    @Bean
    public DeclaredDictI18nLoaderProperties declaredDictI18nLoaderProperties() {
        return new DeclaredDictI18nLoaderProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.loader.declared", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DeclaredDictLoader declaredDictLoader(final DictScanner dictScanner, final DeclaredDictI18nLoaderProperties declaredDictI18nLoaderProperties) {
        return new DeclaredDictLoader(dictScanner, declaredDictI18nLoaderProperties);
    }

}