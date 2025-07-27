package cn.silwings.dicti18n.loader.sql.init;

import cn.silwings.dicti18n.loader.sql.TestApp;
import cn.silwings.dicti18n.loader.sql.init.mjdbc.MockJdbcTemplate;
import cn.silwings.dicti18n.loader.sql.init.schema.DictI18nSchemaInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApp.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DictI18nSchemaInitializerTest {

    public static final String[] SCHEMA_SQL = new String[]
            {
                    "CREATE TABLE IF NOT EXISTS dict_i18n (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "dict_key VARCHAR(512) NOT NULL," +
                            "lang VARCHAR(10) NOT NULL," +
                            "description VARCHAR(1024) NOT NULL," +
                            "enabled TINYINT NOT NULL DEFAULT 1 COMMENT 'Enable or not: 1-Enable, 0-Disable'," +
                            "UNIQUE KEY uidx_dicti18n_dictkey_lang (dict_key, lang)" +
                            ") ENGINE=InnoDB;",
                    "CREATE INDEX idx_dicti18n_dictkey ON dict_i18n (dict_key);",
                    "CREATE INDEX idx_dicti18n_lang ON dict_i18n (lang);"
            };

    @Autowired
    private DictI18nSchemaInitializer dictI18nSchemaInitializer;
    @Autowired
    private MockJdbcTemplate jdbcTemplate;

    @Test
    public void testInitialize() {
        this.dictI18nSchemaInitializer.initialize();
        for (int i = 0; i < this.jdbcTemplate.getSchemaSqls().size(); i++) {
            final String sql = this.jdbcTemplate.getSchemaSqls().get(i);
            if (i < SCHEMA_SQL.length) {
                assertEquals(SCHEMA_SQL[i], sql);
            }
        }
    }

}
