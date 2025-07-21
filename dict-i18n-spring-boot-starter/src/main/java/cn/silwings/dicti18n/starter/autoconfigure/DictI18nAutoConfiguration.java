package cn.silwings.dicti18n.starter.autoconfigure;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import cn.silwings.dicti18n.processor.DictI18nProcessor;
import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
import cn.silwings.dicti18n.provider.DictI18nProvider;
import cn.silwings.dicti18n.sorter.DictLoaderConfigSorter;
import cn.silwings.dicti18n.sorter.DictLoaderSorter;
import cn.silwings.dicti18n.starter.check.UniqueDictNameChecker;
import cn.silwings.dicti18n.starter.config.DefaultLanguageProvider;
import cn.silwings.dicti18n.starter.config.DictI18nStarterProperties;
import cn.silwings.dicti18n.starter.config.LanguageProvider;
import cn.silwings.dicti18n.starter.endpoint.DictItemsEndpointHandler;
import cn.silwings.dicti18n.starter.enhancer.DictI18nResponseEnhancer;
import cn.silwings.dicti18n.starter.enhancer.filter.AlwaysTrueDictI18nResponseFilter;
import cn.silwings.dicti18n.starter.enhancer.filter.DictI18nResponseFilter;
import cn.silwings.dicti18n.starter.sorter.SpringDictLoaderSorter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
public class DictI18nAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n")
    public DictI18nProperties dictI18nProperties() {
        return new DictI18nProperties();
    }

    @Bean
    public DictI18nStarterProperties dictI18nCheckProperties() {
        return new DictI18nStarterProperties();
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
    public CompositeDictI18nProvider compositeDictI18nProvider(final DictLoaderSorter dictLoaderSorter) {
        return new CompositeDictI18nProvider(dictLoaderSorter);
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
                                                             final DictI18nStarterProperties dictI18nStarterProperties) {
        return new DictI18nResponseEnhancer(dictI18nProcessor, languageProvider, dictI18nResponseFilter, dictI18nStarterProperties);
    }

    @Bean
    public DictScanner dictScanner() {
        return new DictScanner();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.starter.check.unique-dict-name", name = "enabled", havingValue = "true", matchIfMissing = true)
    public UniqueDictNameChecker dictNameUniqueChecker(final DictScanner dictScanner, final DictI18nStarterProperties dictI18NStarterProperties) {
        return new UniqueDictNameChecker(dictScanner, dictI18NStarterProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.starter.endpoint.dict-items", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DictItemsEndpointHandler dictItemsHandler(final DictScanner dictScanner,
                                                     final LanguageProvider languageProvider,
                                                     final DictI18nProperties dictI18nProperties,
                                                     final CompositeDictI18nProvider compositeDictI18nProvider,
                                                     final DictI18nStarterProperties dictI18nStarterProperties,
                                                     final RequestMappingHandlerAdapter handlerAdapter) {
        return new DictItemsEndpointHandler(dictScanner,
                languageProvider,
                dictI18nProperties,
                compositeDictI18nProvider,
                dictI18nStarterProperties,
                handlerAdapter);
    }

    @Bean
    @ConditionalOnBean(DictItemsEndpointHandler.class)
    public HandlerMapping dictItemsHandlerMapping(final DictItemsEndpointHandler dictItemsEndpointHandler) {
        return this.buildHandlerMapping("/dict-i18n/api/items", dictItemsEndpointHandler);
    }

    private HandlerMapping buildHandlerMapping(final String path, final HttpRequestHandler handler) {
        final Map<String, HttpRequestHandler> urlMap = new HashMap<>();
        urlMap.put(path, handler);
        final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(0);
        return mapping;
    }
}