package cn.silwings.dicti18n.loader.sql.config;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.sql.SqlDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.cache.DictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.cache.GuavaDictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.cache.NoCacheDictCacheProvider;
import cn.silwings.dicti18n.loader.sql.init.data.DictI18nSqlDataInitRunner;
import cn.silwings.dicti18n.loader.sql.init.data.DictI18nSqlDataInitializer;
import cn.silwings.dicti18n.loader.sql.init.schema.DictI18nSchemaInitRunner;
import cn.silwings.dicti18n.loader.sql.init.schema.DictI18nSchemaInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableConfigurationProperties(SqlDictI18nLoaderProperties.class)
@ConditionalOnProperty(prefix = "dict-i18n.loader.sql", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SqlDictI18nLoaderAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SqlDictI18nLoaderAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(DictI18nLoaderCacheProvider.class)
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "true")
    public GuavaDictI18nLoaderCacheProvider guavaDictI18nLoaderCacheProvider(final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        log.info("[DictI18n] Using GuavaDictI18nLoaderCacheProvider.");
        return new GuavaDictI18nLoaderCacheProvider(sqlDictI18nLoaderProperties.getCache());
    }

    @Bean
    @ConditionalOnMissingBean(DictI18nLoaderCacheProvider.class)
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "false", matchIfMissing = true)
    public NoCacheDictCacheProvider noCacheDictCacheProvider() {
        log.info("[DictI18n] Using NoCacheDictCacheProvider");
        return new NoCacheDictCacheProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.schema.enabled", havingValue = "true")
    public DictI18nSchemaInitializer dictI18nSchemaInitializer(final JdbcTemplate jdbcTemplate) {
        return new DictI18nSchemaInitializer(jdbcTemplate);
    }

    @Bean
    @ConditionalOnBean(DictI18nSchemaInitializer.class)
    public DictI18nSchemaInitRunner dictI18nSchemaInitRunner(final DictI18nSchemaInitializer dictI18nSchemaInitializer, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        return new DictI18nSchemaInitRunner(dictI18nSchemaInitializer, sqlDictI18nLoaderProperties.getSchema());
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.preload.enabled", havingValue = "true")
    public DictI18nSqlDataInitializer dictI18nSqlDataInitializer(final JdbcTemplate jdbcTemplate) {
        return new DictI18nSqlDataInitializer(jdbcTemplate);
    }

    @Bean
    @ConditionalOnBean(DictI18nSqlDataInitializer.class)
    public DictI18nSqlDataInitRunner dictI18nSqlDataInitRunner(final SqlDictI18nLoader sqlDictI18nLoader, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties, final DictFileParser dictFileParser, final DictI18nSqlDataInitializer dictI18NSqlDataInitializer) {
        return new DictI18nSqlDataInitRunner(sqlDictI18nLoader, sqlDictI18nLoaderProperties, dictFileParser, dictI18NSqlDataInitializer);
    }

    @Bean
    public SqlDictI18nLoader sqlDictI18nLoader(final JdbcTemplate jdbcTemplate, final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider) {
        return new SqlDictI18nLoader(jdbcTemplate, dictI18nLoaderCacheProvider);
    }
}