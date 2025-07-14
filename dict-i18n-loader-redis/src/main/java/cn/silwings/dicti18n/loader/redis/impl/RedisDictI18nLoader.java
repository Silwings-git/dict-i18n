package cn.silwings.dicti18n.loader.redis.impl;


import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.redis.config.DictI18nRedisProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class RedisDictI18nLoader implements DictI18nLoader {

    private final DictI18nRedisProperties dictI18nRedisProperties;

    private final StringRedisTemplate redisTemplate;

    public RedisDictI18nLoader(final DictI18nRedisProperties dictI18nRedisProperties, final StringRedisTemplate redisTemplate) {
        this.dictI18nRedisProperties = dictI18nRedisProperties;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void preload() {
        // 预加载redis数据


    }


    @Override
    public String loaderName() {
        return "redis";
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        final String redisKey = String.format("dict_i18n:%s:%s", lang, key);
        return Optional.ofNullable(this.redisTemplate.opsForValue().get(redisKey));
    }
}