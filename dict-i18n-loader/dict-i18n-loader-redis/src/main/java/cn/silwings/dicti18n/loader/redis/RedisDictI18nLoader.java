package cn.silwings.dicti18n.loader.redis;


import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class RedisDictI18nLoader implements DictI18nLoader {

    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;

    private final StringRedisTemplate redisTemplate;

    public RedisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate redisTemplate) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void preload() {
        // 预加载redis数据

        // TODO_Silwings: 2025/7/15 待实现

    }


    @Override
    public String loaderName() {
        return "redis";
    }

    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        final String redisKey = String.format("dict_i18n:%s:%s", lang, dictKey);
        return Optional.ofNullable(this.redisTemplate.opsForValue().get(redisKey));
    }
}