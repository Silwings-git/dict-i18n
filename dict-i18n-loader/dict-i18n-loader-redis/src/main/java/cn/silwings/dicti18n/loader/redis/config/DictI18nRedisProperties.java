package cn.silwings.dicti18n.loader.redis.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictI18nRedisProperties {
    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private String locationPattern = "classpath:dict_i18n/dict_*.yml";

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