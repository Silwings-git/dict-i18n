package cn.silwings.dicti18n.loader.redis.config;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.redis.RedisDictI18nLoader;
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
    public RedisDictI18nLoader redisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate stringRedisTemplate, final DictFileParser dictFileParser) {
        return new RedisDictI18nLoader(redisDictI18nLoaderProperties, stringRedisTemplate, dictFileParser);
    }

}