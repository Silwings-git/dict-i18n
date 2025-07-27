package cn.silwings.dicti18n.loader.sql;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.cache.DictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.db.SQLTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Optional;

/**
 * SQL Dictionary Internationalization Loader
 * Responsible for loading dictionary data from relational databases and supporting caching mechanisms to improve performance
 */
public class SqlDictI18nLoader implements ClassPathDictI18nLoader {

    private static final Logger log = LoggerFactory.getLogger(SqlDictI18nLoader.class);
    private final SQLTemplate sqlTemplate;
    private final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider;

    public SqlDictI18nLoader(final SQLTemplate sqlTemplate, final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider) {
        this.sqlTemplate = sqlTemplate;
        this.dictI18nLoaderCacheProvider = dictI18nLoaderCacheProvider;
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public String loaderName() {
        return "sql";
    }

    /**
     * Get the internationalized description for the specified language and dictionary key
     * Fetch from cache first, perform a database query on cache miss.
     */
    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        return this.dictI18nLoaderCacheProvider.getDesc(lang, dictKey, this.getDatabaseQuery());
    }

    /**
     * Get database query function
     * This function defines how to query dictionary data from the database.
     *
     * @return database query function
     */
    public DictI18nDatabaseQuery getDatabaseQuery() {
        return (lang, dictKey) -> {
            try {
                final String sql = "SELECT description FROM dict_i18n WHERE dict_key = ? AND lang = ? AND enabled = 1 LIMIT 1";

                final String description = this.sqlTemplate.queryForObject(sql, String.class, Arrays.asList(dictKey, lang));

                return Optional.of(description);
            } catch (EmptyResultDataAccessException e) {
                return Optional.empty();
            }
        };
    }

}