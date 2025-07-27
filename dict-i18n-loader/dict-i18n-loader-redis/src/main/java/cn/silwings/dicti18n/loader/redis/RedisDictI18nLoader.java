package cn.silwings.dicti18n.loader.redis;


import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.enums.ErrorHandlingStrategy;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

/**
 * Redis dictionary internationalization loader
 * Responsible for loading dictionary data into Redis and providing the functionality to retrieve internationalized dictionary entries from Redis.
 */
public class RedisDictI18nLoader implements ClassPathDictI18nLoader {

    private static final Logger log = LoggerFactory.getLogger(RedisDictI18nLoader.class);

    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;

    private final StringRedisTemplate redisTemplate;

    public RedisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate redisTemplate) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Logger getLog() {
        return log;
    }

    /**
     * Generate Redis storage keys in the format: prefix + language + ":" + processed key
     */
    public String processKey(String lang, String dictKey) {
        return this.redisDictI18nLoaderProperties.getKeyPrefix() + ":" + lang + ":" + this.redisDictI18nLoaderProperties.processKey(dictKey);
    }

    @Override
    public String loaderName() {
        return "redis";
    }

    /**
     * Retrieve dictionary values for specified language and key from Redis
     */
    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        final String redisKey = this.processKey(lang, dictKey);
        try {
            return Optional.ofNullable(this.redisTemplate.opsForValue().get(redisKey));
        } catch (Exception e) {
            if (ErrorHandlingStrategy.IGNORE.equals(redisDictI18nLoaderProperties.getErrorHandlingStrategy())) {
                log.debug("[DictI18n] Redis query failure: {}", e.getMessage(), e);
                return Optional.empty();
            }
            throw e;
        }
    }
}