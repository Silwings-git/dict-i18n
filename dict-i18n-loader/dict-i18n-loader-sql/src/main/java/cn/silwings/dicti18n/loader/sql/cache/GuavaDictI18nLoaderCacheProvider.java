package cn.silwings.dicti18n.loader.sql.cache;

import cn.silwings.dicti18n.loader.sql.DictI18nDatabaseQuery;
import cn.silwings.dicti18n.loader.sql.config.SqlDictI18nLoaderProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaDictI18nLoaderCacheProvider implements DictI18nLoaderCacheProvider {

    private static final Logger log = LoggerFactory.getLogger(GuavaDictI18nLoaderCacheProvider.class);
    private final Cache<String, Optional<String>> cache;

    public GuavaDictI18nLoaderCacheProvider(final SqlDictI18nLoaderProperties.SqlDictI18nLoaderCacheProperties sqlDictI18nLoaderCacheProperties) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(sqlDictI18nLoaderCacheProperties.getMaximumSize())
                .expireAfterWrite(sqlDictI18nLoaderCacheProperties.getExpireAfterWriteSeconds(), TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<String> getDesc(final String lang, final String key, final DictI18nDatabaseQuery dbQuery) {
        final String cacheKey = this.generateCacheKey(lang, key);
        try {
            return this.cache.get(cacheKey, () -> {
                try {
                    return dbQuery.select(lang, key);
                } catch (Exception e) {
                    log.error("[DictI18n] Failed to query internationalized dictionary data from the database: {}", e.getMessage(), e);
                    return Optional.empty();
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCacheKey(String lang, String key) {
        return lang + "." + key;
    }
}
