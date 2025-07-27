package cn.silwings.dicti18n.loader.sql.cache;

import cn.silwings.dicti18n.loader.cache.DictDescGetter;
import cn.silwings.dicti18n.loader.cache.DictI18nLoaderCacheProvider;

import java.util.Optional;

public class NoCacheDictCacheProvider implements DictI18nLoaderCacheProvider {
    @Override
    public Optional<String> getDesc(final String lang, final String dictKey, final DictDescGetter dbQuery) {
        return dbQuery.get(lang, dictKey);
    }
}