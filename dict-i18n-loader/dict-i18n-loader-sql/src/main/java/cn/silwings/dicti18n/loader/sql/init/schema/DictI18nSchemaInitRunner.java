package cn.silwings.dicti18n.loader.sql.init.schema;

import cn.silwings.dicti18n.loader.sql.config.SqlDictI18nLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

public class DictI18nSchemaInitRunner implements ApplicationRunner, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DictI18nSchemaInitRunner.class);
    public static final int ORDER = Ordered.LOWEST_PRECEDENCE - 100;
    private final DictI18nSchemaInitializer dictI18nSchemaInitializer;
    private final SqlDictI18nLoaderProperties.SqlDictI18nLoaderSqlSchemaInitProperties properties;

    public DictI18nSchemaInitRunner(final DictI18nSchemaInitializer dictI18nSchemaInitializer, final SqlDictI18nLoaderProperties.SqlDictI18nLoaderSqlSchemaInitProperties properties) {
        this.dictI18nSchemaInitializer = dictI18nSchemaInitializer;
        this.properties = properties;
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
