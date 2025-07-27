package cn.silwings.dicti18n.loader.sql;

import cn.silwings.dicti18n.loader.cache.DictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.db.SQLTemplate;
import cn.silwings.dicti18n.loader.sql.init.data.DictI18nSqlDataInitializer;
import cn.silwings.dicti18n.loader.sql.init.mjdbc.MockJdbcTemplate;
import cn.silwings.dicti18n.loader.sql.init.schema.DictI18nSchemaInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApp {

    @Bean
    public MockJdbcTemplate mockJdbcTemplate() {
        return new MockJdbcTemplate();
    }

    @Bean
    public DictI18nSchemaInitializer dictI18nSchemaInitializer(final SQLTemplate sqlTemplate) {
        return new DictI18nSchemaInitializer(sqlTemplate);
    }

    @Bean
    public DictI18nSqlDataInitializer dictI18nSqlDataInitializer(final SQLTemplate sqlTemplate) {
        return new DictI18nSqlDataInitializer(sqlTemplate);
    }

    //    @Bean
    public SqlDictI18nLoader sqlDictI18nLoader(final SQLTemplate sqlTemplate, final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider) {
        return new SqlDictI18nLoader(sqlTemplate, dictI18nLoaderCacheProvider);
    }
}
