package cn.silwings.dicti18n.loader.sql.cache;

import cn.silwings.dicti18n.loader.sql.DictI18nDatabaseQuery;

import java.util.Optional;

public class NoCacheDictCacheProvider implements DictI18nLoaderCacheProvider {
    @Override
    public Optional<String> getDesc(final String lang, final String dictKey, final DictI18nDatabaseQuery dbQuery) {
        return dbQuery.select(lang, dictKey);
    }
}