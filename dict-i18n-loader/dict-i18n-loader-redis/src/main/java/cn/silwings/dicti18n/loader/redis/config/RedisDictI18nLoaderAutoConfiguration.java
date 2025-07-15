package cn.silwings.dicti18n.loader.redis.config;

import cn.silwings.dicti18n.loader.redis.impl.RedisDictI18nLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisDictI18nLoaderAutoConfiguration {

    @Bean
    public RedisDictI18nLoaderProperties redisDictI18nLoaderProperties() {
        return new RedisDictI18nLoaderProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dict-i18n.loader.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedisDictI18nLoader redisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate stringRedisTemplate) {
        return new RedisDictI18nLoader(redisDictI18nLoaderProperties, stringRedisTemplate);
    }

}