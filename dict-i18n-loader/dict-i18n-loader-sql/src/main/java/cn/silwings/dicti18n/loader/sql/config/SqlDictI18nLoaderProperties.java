package cn.silwings.dicti18n.loader.sql.config;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.enums.PreLoadMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.sql")
public class SqlDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {
    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private List<String> locationPatterns = ClassPathDictI18nLoader.LOCATION_PATTERNS;

    /**
     * Preload properties.
     */
    private SqlDictI18nLoaderPreloadProperties preload = new SqlDictI18nLoaderPreloadProperties();

    /**
     * Cache properties.
     */
    private SqlDictI18nLoaderCacheProperties cache = new SqlDictI18nLoaderCacheProperties();

    /**
     * SQL schema initialization properties.
     */
    private SqlDictI18nLoaderSqlSchemaInitProperties schema = new SqlDictI18nLoaderSqlSchemaInitProperties();

    @Getter
    @Setter
    @ToString
    public static class SqlDictI18nLoaderPreloadProperties {

        /**
         * Whether to load dict data from the resource file into database on startup.
         * Default is false.
         */
        private boolean enabled = false;

        /**
         * Whether to fail fast when loading dict data into database.
         * If true, the application will fail to start if there is an error during loading.
         * If false, it will log the error and continue.
         * Default is true.
         */
        private boolean failFast = true;

        /**
         * Load mode when preloading to database.
         * - FULL: Full overwrite. All keys will be written (even if they exist).
         * - INCREMENTAL: Only write keys that do not exist in database.
         * Default is INCREMENTAL.
         */
        private PreLoadMode preloadMode = PreLoadMode.INCREMENTAL;

    }

    @Getter
    @Setter
    @ToString
    public static class SqlDictI18nLoaderCacheProperties {
        /**
         * Enable cache
         */
        private boolean enabled = false;

        /**
         * Maximum number of cache items
         */
        private int maximumSize = 1000;

        /**
         * Cache expiration time (unit: seconds, default 300s = 5 minutes)
         */
        private long expireAfterWriteSeconds = 300;
    }

    @Getter
    @Setter
    public static class SqlDictI18nLoaderSqlSchemaInitProperties {
        /**
         * Whether to enable schema initialization (create tables + create indexes)
         */
        private boolean enabled = false;
    }

}