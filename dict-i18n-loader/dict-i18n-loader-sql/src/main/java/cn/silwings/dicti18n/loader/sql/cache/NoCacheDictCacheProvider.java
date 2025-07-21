package cn.silwings.dicti18n.loader.sql.cache;

import java.util.Optional;
import java.util.function.Supplier;

public class NoCacheDictCacheProvider implements DictI18nLoaderCacheProvider {
    @Override
    public Optional<String> getDesc(final String lang, final String key, final Supplier<Optional<String>> dbQuery) {
        return dbQuery.get();
    }
}