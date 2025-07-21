package cn.silwings.dicti18n.loader.sql;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.cache.DictI18nLoaderCacheProvider;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

@AllArgsConstructor
public class SqlDictI18nLoader implements ClassPathDictI18nLoader {

    private final Logger log = LoggerFactory.getLogger(SqlDictI18nLoader.class);
    private JdbcTemplate jdbcTemplate;
    private final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider;

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public String loaderName() {
        return "sql";
    }

    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        try {
            final String sql = "SELECT description FROM dict_i18n_items " +
                    "WHERE dict_key = ? AND lang = ? AND enabled = true LIMIT 1";

            final String description = this.jdbcTemplate.queryForObject(sql, String.class, dictKey, lang);

            return Optional.of(description);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}