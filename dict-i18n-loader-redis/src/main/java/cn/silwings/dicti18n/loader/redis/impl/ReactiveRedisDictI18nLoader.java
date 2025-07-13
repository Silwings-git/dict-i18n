package cn.silwings.dicti18n.loader.redis.impl;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.redis.config.DictI18nRedisProperties;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Optional;

/**
 * @ClassName ReactiveRedisDictI18nLoader
 * @Description
 * @Author Silwings
 * @Date 2025/7/13 23:53
 * @Since
 **/
public class ReactiveRedisDictI18nLoader implements DictI18nLoader {

    private final DictI18nRedisProperties dictI18nRedisProperties;

    private final ReactiveStringRedisTemplate redisTemplate;

    public ReactiveRedisDictI18nLoader(final DictI18nRedisProperties dictI18nRedisProperties, final ReactiveStringRedisTemplate redisTemplate) {
        this.dictI18nRedisProperties = dictI18nRedisProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String loaderName() {
        return "redis";
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        return Optional.empty();
    }
}