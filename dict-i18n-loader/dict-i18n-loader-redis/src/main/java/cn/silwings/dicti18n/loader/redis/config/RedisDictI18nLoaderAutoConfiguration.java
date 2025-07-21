package cn.silwings.dicti18n.loader.redis.config;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.redis.RedisDictI18nLoader;
import cn.silwings.dicti18n.loader.redis.init.DictI18nRedisDataInitRunner;
import cn.silwings.dicti18n.loader.redis.init.DictI18nRedisDataInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisDictI18nLoaderProperties.class)
@ConditionalOnProperty(prefix = "dict-i18n.loader.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisDictI18nLoaderAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.redis.preload.enabled", havingValue = "true")
    public DictI18nRedisDataInitializer dictI18nRedisDataInitializer(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final RedisDictI18nLoader redisDictI18nLoader, final DictFileParser dictFileParser, final StringRedisTemplate redisTemplate) {
        return new DictI18nRedisDataInitializer(redisDictI18nLoaderProperties, redisDictI18nLoader, dictFileParser, redisTemplate);
    }

    @Bean
    @ConditionalOnBean(DictI18nRedisDataInitializer.class)
    public DictI18nRedisDataInitRunner dictI18nRedisDataInitRunner(final DictI18nRedisDataInitializer dictI18nRedisDataInitializer) {
        return new DictI18nRedisDataInitRunner(dictI18nRedisDataInitializer);
    }

    @Bean
    public RedisDictI18nLoader redisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate stringRedisTemplate) {
        return new RedisDictI18nLoader(redisDictI18nLoaderProperties, stringRedisTemplate);
    }

}