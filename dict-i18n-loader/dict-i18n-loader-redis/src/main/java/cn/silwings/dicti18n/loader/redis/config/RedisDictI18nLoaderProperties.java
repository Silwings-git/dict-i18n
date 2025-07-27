package cn.silwings.dicti18n.loader.redis.config;

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
@ConfigurationProperties(prefix = "dict-i18n.loader.redis")
public class RedisDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {
    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private List<String> locationPatterns = ClassPathDictI18nLoader.LOCATION_PATTERNS;

    /**
     * Redis Preload Properties
     */
    private RedisDictI18nLoaderPreloadProperties preload = new RedisDictI18nLoaderPreloadProperties();

    @Getter
    @Setter
    @ToString
    public static class RedisDictI18nLoaderPreloadProperties {

        /**
         * Whether to load dict data from the resource file into Redis on startup.
         * Default is false.
         */
        private boolean enabled = false;

        /**
         * Whether to fail fast when loading dict data into Redis.
         * If true, the application will fail to start if there is an error during loading.
         * If false, it will log the error and continue.
         * Default is true.
         */
        private boolean failFast = true;

        /**
         * Load mode when preloading to Redis.
         * - FULL: Full overwrite. All keys will be written (even if they exist).
         * - INCREMENTAL: Only write keys that do not exist in Redis.
         * Default is INCREMENTAL.
         */
        private PreLoadMode preloadMode = PreLoadMode.INCREMENTAL;
    }
}