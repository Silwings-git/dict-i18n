package cn.silwings.dicti18n.provider;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.sorter.DictLoaderSorter;
import cn.silwings.dicti18n.utils.LangFallbackUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A multi-dictionary loader combination provider that aggregates multiple {@link DictI18nLoader} according to the
 * configuration order to achieve internationalization parsing and language degradation support for dictionary text.
 */
public class CompositeDictI18nProvider implements DictI18nProvider {

    private final DictI18nProperties dictI18nProperties;

    /**
     * A collection of loaders that have been sorted, in the order determined by {@link DictLoaderSorter}.
     */
    private final List<DictI18nLoader> orderedLoaders;

    /**
     * Language downgrade chain cache
     */
    private final Map<String, List<String>> fallbackLangChainCache;

    /**
     * Constructors, receive sequencers and configuration items, initialize loader lists.
     *
     * @param sorter             A loader sequencer that controls the order in which the loader is executed
     * @param dictI18nProperties Dictionary configuration properties
     */
    public CompositeDictI18nProvider(final DictLoaderSorter sorter, final DictI18nProperties dictI18nProperties) {
        this.dictI18nProperties = dictI18nProperties;
        this.orderedLoaders = sorter.getOrderedLoaders();
        this.fallbackLangChainCache = new ConcurrentHashMap<>();
        if (this.orderedLoaders.isEmpty()) {
            throw new IllegalArgumentException("Provide at least one DictI18nLoader.");
        }
    }

    /**
     * Get internationalized copywriting with support for language downgrading and default language.
     *
     * @param language User language, e.g. en-US, zh-CN
     * @param dictName Dictionary name, e.g. order_status
     * @param code     Dictionary value, e.g. "pending"
     * @return Copywriting (e.g. pending payment)
     */
    @Override
    public Optional<String> getText(final String language, final String dictName, final String code) {
        for (DictI18nLoader loader : this.orderedLoaders) {
            // The current language chain tries to find
            Optional<String> result = this.getText(language, dictName, code, loader);

            // If the default language is not selected and the default language is configured, the default language is used
            if (!result.isPresent()
                && StringUtils.isNotBlank(this.dictI18nProperties.getDefaultLang())
                && !this.getFallbackLangChain(language).contains(this.dictI18nProperties.getDefaultLang())) {
                result = this.getText(this.dictI18nProperties.getDefaultLang(), dictName, code, loader);
            }
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    /**
     * Look up dictionary entries from the specified loader, traversing the downgrade chain by language.
     *
     * @param language User language
     * @param dictName Dictionary name, e.g. order_status
     * @param code     Dictionary value, e.g. "pending"
     * @param loader   Dictionary loader
     * @return Query results (priority language chain order)
     */
    private Optional<String> getText(final String language, final String dictName, final String code, final DictI18nLoader loader) {
        for (final String lang : this.getFallbackLangChain(language)) {
            final Optional<String> result = loader.get(lang, dictName.concat(".").concat(code))
                    .filter(StringUtils::isNotBlank);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    /**
     * Get the downgrade chain for the specified language, e.g. zh-CN → zh.
     *
     * @param language Original language string
     * @return List of downgraded language chains
     */
    private List<String> getFallbackLangChain(final String language) {
        return this.fallbackLangChainCache.computeIfAbsent(language, LangFallbackUtils::fallbackLangChain);
    }
}