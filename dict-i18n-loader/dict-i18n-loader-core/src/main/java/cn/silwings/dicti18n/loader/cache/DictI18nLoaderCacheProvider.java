package cn.silwings.dicti18n.loader.cache;

import java.util.Optional;

/**
 * Dictionary internationalization loader's cache provider interface.
 * <p>
 * This interface allows customization of the cache strategy for dictionary data, and implementing classes can choose
 * to retrieve dictionary description information from the cache or directly from the data source. By implementing this
 * interface and injecting it into the Spring container, the default caching behavior of supported loaders
 * (such as SqlDictI18nLoader) can be flexibly replaced.
 * </p>
 */
@FunctionalInterface
public interface DictI18nLoaderCacheProvider {

    /**
     * Get the dictionary description from cache or underlying source.
     *
     * @param lang       Language code, e.g. "en", "zh-CN"
     * @param key        Dictionary key, e.g. "order.order_status.PENDING"
     * @param descGetter Supplier to load data if cache miss
     * @return Optional containing description if found, or empty if not found
     */
    Optional<String> getDesc(String lang, String key, DictDescGetter descGetter);

}
