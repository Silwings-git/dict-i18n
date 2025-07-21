package cn.silwings.dicti18n.loader.sql.cache;

import java.util.Optional;
import java.util.function.Supplier;

@FunctionalInterface
public interface DictI18nLoaderCacheProvider {
    /**
     * Get the dictionary description from cache or underlying source.
     *
     * @param lang    Language code, e.g. "en", "zh-CN"
     * @param key     Dictionary key, e.g. "order.order_status.PENDING"
     * @param dbQuery Supplier to load data if cache miss
     * @return Optional containing description if found, or empty if not found
     */
    Optional<String> getDesc(String lang, String key, Supplier<Optional<String>> dbQuery);

}
