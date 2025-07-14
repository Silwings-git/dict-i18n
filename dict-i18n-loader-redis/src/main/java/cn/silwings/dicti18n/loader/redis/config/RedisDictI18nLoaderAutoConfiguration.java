package cn.silwings.dicti18n.loader.redis.config;

import cn.silwings.dicti18n.loader.redis.impl.ReactiveRedisDictI18nLoader;
import cn.silwings.dicti18n.loader.redis.impl.RedisDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnMissingBean(RedisDictI18nLoader.class)
public class RedisDictI18nLoaderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dict-i18n.loader.redis")
    public DictI18nRedisProperties dictI18nRedisProperties() {
        return new DictI18nRedisProperties();
    }

    @Bean
    @ConditionalOnBean(ReactiveStringRedisTemplate.class)
    public ReactiveRedisDictI18nLoader reactiveRedisDictI18nLoader(
            DictI18nRedisProperties dictI18nRedisProperties,
            ReactiveStringRedisTemplate reactiveRedisTemplate) {
        return new ReactiveRedisDictI18nLoader(dictI18nRedisProperties, reactiveRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveRedisDictI18nLoader.class)
    public RedisDictI18nLoader redisDictI18nLoader(
            DictI18nRedisProperties dictI18nRedisProperties,
            StringRedisTemplate stringRedisTemplate) {
        return new RedisDictI18nLoader(dictI18nRedisProperties, stringRedisTemplate);
    }

}