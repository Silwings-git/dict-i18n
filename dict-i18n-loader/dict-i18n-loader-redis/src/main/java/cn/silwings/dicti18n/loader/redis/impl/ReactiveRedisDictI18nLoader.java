package cn.silwings.dicti18n.loader.redis.impl;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Optional;


public class ReactiveRedisDictI18nLoader implements DictI18nLoader {

    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;

    private final ReactiveStringRedisTemplate redisTemplate;

    public ReactiveRedisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final ReactiveStringRedisTemplate redisTemplate) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String loaderName() {
        return "redis";
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        // TODO_Silwings: 2025/7/15 待实现
        return Optional.empty();
    }
}