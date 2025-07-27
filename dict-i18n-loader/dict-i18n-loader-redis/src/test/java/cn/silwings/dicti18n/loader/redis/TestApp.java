package cn.silwings.dicti18n.loader.redis;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.redis.init.DictI18nRedisDataInitializer;
import cn.silwings.dicti18n.loader.redis.init.MockStringRedisTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class TestApp {

    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties) {
        final MockStringRedisTemplate mockStringRedisTemplate = new MockStringRedisTemplate(redisDictI18nLoaderProperties);
        mockStringRedisTemplate.setConnectionFactory(new LettuceConnectionFactory());
        return mockStringRedisTemplate;
    }

    @Bean
    public DictI18nRedisDataInitializer dictI18nRedisDataInitializer(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final RedisDictI18nLoader redisDictI18nLoader, final DictFileParser dictFileParser, final StringRedisTemplate redisTemplate) {
        return new DictI18nRedisDataInitializer(redisDictI18nLoaderProperties, redisDictI18nLoader, dictFileParser, redisTemplate);
    }
}
