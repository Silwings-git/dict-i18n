package cn.silwings.dicti18n.loader.sql.init.schema;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

public class DictI18nSchemaInitRunner implements ApplicationRunner, Ordered {

    public static final int ORDER = Ordered.LOWEST_PRECEDENCE - 100;
    private final DictI18nSchemaInitializer dictI18nSchemaInitializer;

    public DictI18nSchemaInitRunner(final DictI18nSchemaInitializer dictI18nSchemaInitializer) {
        this.dictI18nSchemaInitializer = dictI18nSchemaInitializer;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void run(final ApplicationArguments args) {
        this.dictI18nSchemaInitializer.initialize();
    }
}
