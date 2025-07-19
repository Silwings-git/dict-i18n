package cn.silwings.dicti18n.loader.redis.config;

import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.redis")
public class RedisDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {
    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private List<String> locationPatterns = Arrays.asList("classpath:dict_i18n/dict_*.yml", "classpath:dict_i18n/dict_*.properties");

    /**
     * Whether to load dict data from the resource file into Redis on startup.
     * Default is false.
     */
    private boolean preload = false;

    /**
     * Load mode when preloading to Redis.
     * - FULL: Full overwrite. All keys will be written (even if they exist).
     * - INCREMENTAL: Only write keys that do not exist in Redis.
     * Default is INCREMENTAL.
     */
    private LoadMode preloadMode = LoadMode.INCREMENTAL;

    public enum LoadMode {
        FULL,
        INCREMENTAL
    }

}