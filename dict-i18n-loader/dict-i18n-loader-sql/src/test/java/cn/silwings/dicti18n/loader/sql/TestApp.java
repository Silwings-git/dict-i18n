package cn.silwings.dicti18n.loader.sql;

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
}
