package cn.silwings.dicti18n.loader.redis.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class DictI18nRedisDataInitRunner implements ApplicationRunner {

    private final DictI18nRedisDataInitializer dictI18nRedisDataInitializer;

    public DictI18nRedisDataInitRunner(final DictI18nRedisDataInitializer dictI18nRedisDataInitializer) {
        this.dictI18nRedisDataInitializer = dictI18nRedisDataInitializer;
    }

    @Override
    public void run(final ApplicationArguments args) {
        this.dictI18nRedisDataInitializer.preload();
    }
}