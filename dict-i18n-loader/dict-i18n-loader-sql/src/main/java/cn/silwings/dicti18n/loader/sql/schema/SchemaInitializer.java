package cn.silwings.dicti18n.loader.sql.schema;

import cn.silwings.dicti18n.loader.sql.config.SqlDictI18nLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SchemaInitializer {

    private final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);
    private final JdbcTemplate jdbcTemplate;
    private final SqlDictI18nLoaderProperties.SqlDictI18nLoaderSqlSchemaInitProperties properties;

    public SchemaInitializer(final JdbcTemplate jdbcTemplate, SqlDictI18nLoaderProperties.SqlDictI18nLoaderSqlSchemaInitProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public void initialize() {
        if (!this.properties.isEnabled()) {
            log.info("[DictI18n] Schema initialization is disabled.");
            return;
        }
        final String databaseProductName = this.getDatabaseProductName();
        if (null == databaseProductName) {
            throw new IllegalStateException("[DictI18n] Failed to determine database product name");
        }

        log.info("[DictI18n] Starting schema initialization for database: {}", databaseProductName);

        final String[] sqls = this.getInitSqlByDatabase(databaseProductName);
        for (String sql : sqls) {
            try {
                log.debug("[DictI18n] Executing SQL: {}", sql);
                this.jdbcTemplate.execute(sql);
            } catch (DataAccessException e) {
                final String message = e.getMessage();
                if (null == message || !(message.contains("Duplicate") || message.contains("already exists"))) {
                    log.error("[DictI18n] Failed to execute SQL: {}", sql, e);
                    throw e;
                } else {
                    log.debug("[DictI18n] Ignored duplicate-related error for SQL: {}", sql);
                }
            }
        }
        log.info("[DictI18n] Completed schema initialization for database: {}", databaseProductName);
    }

    private String getDatabaseProductName() {
        try {
            final DataSource dataSource = this.jdbcTemplate.getDataSource();
            if (null == dataSource) {
                throw new IllegalStateException("[DictI18n] JdbcTemplate has no DataSource");
            }
            final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            return metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException("[DictI18n] Failed to get database product name", e);
        }
    }

    private String[] getInitSqlByDatabase(final String databaseProductName) {
        switch (databaseProductName.toLowerCase()) {
            case "mysql":
                return new String[]{
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
            case "postgresql":
                return new String[]{
                        "CREATE TABLE IF NOT EXISTS dict_i18n (" +
                                "id BIGSERIAL PRIMARY KEY," +
                                "dict_key VARCHAR(512) NOT NULL," +
                                "lang VARCHAR(10) NOT NULL," +
                                "description VARCHAR(1024) NOT NULL," +
                                "enabled SMALLINT NOT NULL DEFAULT 1," +
                                "UNIQUE (dict_key, lang)" +
                                ");",
                        "CREATE INDEX IF NOT EXISTS idx_dicti18n_dictkey ON dict_i18n (dict_key);",
                        "CREATE INDEX IF NOT EXISTS idx_dicti18n_lang ON dict_i18n (lang);"
                };
            case "sqlite":
                return new String[]{
                        "CREATE TABLE IF NOT EXISTS dict_i18n (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "dict_key TEXT NOT NULL," +
                                "lang TEXT NOT NULL," +
                                "description TEXT NOT NULL," +
                                "enabled INTEGER NOT NULL DEFAULT 1 CHECK (enabled IN (0, 1))," +
                                "UNIQUE(dict_key, lang)" +
                                ");",
                        "CREATE INDEX IF NOT EXISTS idx_dicti18n_dictkey ON dict_i18n (dict_key);",
                        "CREATE INDEX IF NOT EXISTS idx_dicti18n_lang ON dict_i18n (lang);"
                };
            default:
                throw new UnsupportedOperationException("[DictI18n] Unsupported database: " + databaseProductName);
        }
    }
}
