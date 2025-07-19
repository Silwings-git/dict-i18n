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

    public static final String FALLBACK_LOCALE_KEY = "";

    private final DictI18nProperties dictI18nProperties;

    /**
     * A collection of loaders that have been sorted, in the order determined by {@link DictLoaderSorter}.
     */
    private final List<DictI18nLoader> loaders;

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
        this.loaders = sorter.getOrderedLoaders();
        this.fallbackLangChainCache = new ConcurrentHashMap<>();
        if (this.loaders.isEmpty()) {
            throw new IllegalArgumentException("Provide at least one DictI18nLoader.");
        }
    }

    /**
     * Retrieve internationalized copy (dictionary text) for the specified language.
     * Supports language fallback lookup (e.g., zh-CN -> zh) and also supports default language and fallback language configuration.
     * <p>
     * The search order is as follows (in the sequence of loaders):
     * 1. Search from the fallback chain of the current language (e.g., en-US -> en)
     * 2. If not found, attempt to search using the configured default language (defaultLang).
     * 3. If still not found, try looking it up with the special fallback key ("").
     * <p>
     * Once any step in a loader successfully finds a result, it immediately returns that result.
     *
     * @param language User language, such as "en-US", "zh-CN"
     * @param dictName Dictionary name, such as "order_status"
     * @param code     Dictionary keys, such as "pending"
     * @return The dictionary description in the corresponding language, or returns Optional.empty() if not found.
     */
    @Override
    public Optional<String> getText(final String language, final String dictName, final String code) {

        final List<String> langChain = this.getFallbackLangChain(language);
        final String defaultLang = this.dictI18nProperties.getDefaultLang();
        final boolean includeDefaultLang = !langChain.contains(defaultLang);

        for (DictI18nLoader loader : this.loaders) {

            // Search from the fallback chain of the current language
            Optional<String> dictDesc = this.getTextFromLoader(langChain, dictName, code, loader);

            // attempt to search using the configured default language
            if (!dictDesc.isPresent() && includeDefaultLang) {
                dictDesc = this.getTextFromLoader(this.getFallbackLangChain(defaultLang), dictName, code, loader);
            }

            // try looking it up with the special fallback key ("")
            if (!dictDesc.isPresent() && !FALLBACK_LOCALE_KEY.equals(defaultLang)) {
                dictDesc = this.getTextFromLoader(this.getFallbackLangChain(FALLBACK_LOCALE_KEY), dictName, code, loader);
            }

            if (dictDesc.isPresent()) {
                return dictDesc;
            }
        }
        return Optional.empty();
    }

    /**
     * From the given language chain, sequentially attempt to retrieve the internationalized description of the specified dictionary item from the loader.
     * <p>
     * Once a non-empty description is found in any language, return it immediately; if no descriptions are found or the result is blank across all languages, return Optional.empty.
     *
     * @param langChain Language fallback chains, such as ["zh-CN", "zh"]
     * @param dictName  Dictionary name, such as "order_status"
     * @param code      Dictionary key-value, such as "pending"
     * @param loader    The DictI18nLoader currently being attempted to query
     * @return Description of successful retrieval; returns Optional.empty if none match.
     */
    private Optional<String> getTextFromLoader(final List<String> langChain, final String dictName, final String code, final DictI18nLoader loader) {
        for (final String lang : langChain) {
            final Optional<String> result = loader.get(lang, this.resolveKey(dictName, code)).filter(StringUtils::isNotBlank);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    /**
     * Construct the dictionary key name for loader queries, typically formed by concatenating the dictionary name with the code.
     * <p>
     * Example: dictName = "order_status", code = "pending", return "order_status.pending"
     *
     * @param dictName dictionary name
     * @param code     Dictionary key-value, such as "pending"
     * @return spliced key name
     */
    private String resolveKey(final String dictName, final String code) {
        return dictName + "." + code;
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