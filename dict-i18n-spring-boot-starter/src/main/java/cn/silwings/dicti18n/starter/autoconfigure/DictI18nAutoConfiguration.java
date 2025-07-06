package cn.silwings.dicti18n.starter.autoconfigure;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.processor.DictI18nProcessor;
import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
import cn.silwings.dicti18n.provider.DictI18nProvider;
import cn.silwings.dicti18n.sorter.DictLoaderConfigSorter;
import cn.silwings.dicti18n.sorter.DictLoaderSorter;
import cn.silwings.dicti18n.starter.advice.DictI18nResponseEnhancer;
import cn.silwings.dicti18n.starter.advice.filter.AlwaysTrueDictI18nResponseFilter;
import cn.silwings.dicti18n.starter.advice.filter.DictI18nResponseFilter;
import cn.silwings.dicti18n.starter.check.DictNameUniqueChecker;
import cn.silwings.dicti18n.starter.config.DefaultLanguageProvider;
import cn.silwings.dicti18n.starter.config.DictI18nCheckProperties;
import cn.silwings.dicti18n.starter.config.DictI18nResponseEnhancerProperties;
import cn.silwings.dicti18n.starter.config.LanguageProvider;
import cn.silwings.dicti18n.starter.sorter.SpringDictLoaderSorter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
public class DictI18nAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n")
    public DictI18nProperties dictI18nProperties() {
        return new DictI18nProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.response-enhancer")
    public DictI18nResponseEnhancerProperties dictI18nResponseEnhancerProperties() {
        return new DictI18nResponseEnhancerProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.check")
    public DictI18nCheckProperties dictI18nCheckProperties() {
        return new DictI18nCheckProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n", name = "loader-order[0]")
    public DictLoaderSorter dictLoaderConfigSorter(final DictI18nProperties dictI18nProperties, final List<DictI18nLoader> dictI18nLoaders) {
        return new DictLoaderConfigSorter(dictI18nProperties, dictI18nLoaders);
    }

    @Bean
    @ConditionalOnMissingBean(DictLoaderSorter.class)
    public DictLoaderSorter springDictLoaderSorter(final List<DictI18nLoader> dictI18nLoaders) {
        return new SpringDictLoaderSorter(dictI18nLoaders);
    }

    @Bean
    public CompositeDictI18nProvider compositeDictI18nProvider(final DictLoaderSorter dictLoaderSorter, final DictI18nProperties dictI18nProperties) {
        return new CompositeDictI18nProvider(dictLoaderSorter, dictI18nProperties);
    }

    @Bean
    public DictI18nProcessor dictI18nProcessor(final DictI18nProvider provider, final DictI18nProperties properties) {
        return new DictI18nProcessor(provider, properties);
    }

    @Bean
    @ConditionalOnMissingBean(LanguageProvider.class)
    public LanguageProvider defaultLangProvider() {
        return new DefaultLanguageProvider();
    }

    @Bean
    @ConditionalOnMissingBean(DictI18nResponseFilter.class)
    public DictI18nResponseFilter dictI18nResponseFilter() {
        return new AlwaysTrueDictI18nResponseFilter();
    }

    @Bean
    public DictI18nResponseEnhancer dictI18nResponseEnhancer(final DictI18nProcessor dictI18nProcessor,
                                                             final LanguageProvider languageProvider,
                                                             final DictI18nResponseFilter dictI18nResponseFilter,
                                                             final DictI18nResponseEnhancerProperties dictI18nResponseEnhancerProperties) {
        return new DictI18nResponseEnhancer(dictI18nProcessor, languageProvider, dictI18nResponseFilter, dictI18nResponseEnhancerProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.check", name = "enable-dict-name-unique-check", havingValue = "true")
    public DictNameUniqueChecker dictNameUniqueChecker(final DictI18nCheckProperties dictI18nCheckProperties) {
        return new DictNameUniqueChecker(dictI18nCheckProperties);
    }
}