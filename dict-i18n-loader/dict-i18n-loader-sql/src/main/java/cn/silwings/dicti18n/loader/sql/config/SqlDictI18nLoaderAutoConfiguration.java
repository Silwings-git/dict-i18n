package cn.silwings.dicti18n.loader.sql.config;

import cn.silwings.dicti18n.loader.cache.DictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.sql.SqlDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.cache.GuavaDictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.cache.NoCacheDictCacheProvider;
import cn.silwings.dicti18n.loader.sql.db.JdbcSQLTemplate;
import cn.silwings.dicti18n.loader.sql.db.SQLTemplate;
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
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "true", matchIfMissing = true)
    public GuavaDictI18nLoaderCacheProvider guavaDictI18nLoaderCacheProvider(final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        log.info("[DictI18n] Using GuavaDictI18nLoaderCacheProvider.");
        return new GuavaDictI18nLoaderCacheProvider(sqlDictI18nLoaderProperties.getCache());
    }

    @Bean
    @ConditionalOnMissingBean(DictI18nLoaderCacheProvider.class)
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "false")
    public NoCacheDictCacheProvider noCacheDictCacheProvider() {
        log.info("[DictI18n] Using NoCacheDictCacheProvider");
        return new NoCacheDictCacheProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.schema.enabled", havingValue = "true")
    public DictI18nSchemaInitializer dictI18nSchemaInitializer(final SQLTemplate sqlTemplate) {
        return new DictI18nSchemaInitializer(sqlTemplate);
    }

    @Bean
    @ConditionalOnBean(DictI18nSchemaInitializer.class)
    public DictI18nSchemaInitRunner dictI18nSchemaInitRunner(final DictI18nSchemaInitializer dictI18nSchemaInitializer) {
        return new DictI18nSchemaInitRunner(dictI18nSchemaInitializer);
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.preload.enabled", havingValue = "true")
    public DictI18nSqlDataInitializer dictI18nSqlDataInitializer(final SQLTemplate sqlTemplate) {
        return new DictI18nSqlDataInitializer(sqlTemplate);
    }

    @Bean
    @ConditionalOnBean(DictI18nSqlDataInitializer.class)
    public DictI18nSqlDataInitRunner dictI18nSqlDataInitRunner(final SqlDictI18nLoader sqlDictI18nLoader, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties, final DictFileParser dictFileParser, final DictI18nSqlDataInitializer dictI18NSqlDataInitializer) {
        return new DictI18nSqlDataInitRunner(sqlDictI18nLoader, sqlDictI18nLoaderProperties, dictFileParser, dictI18NSqlDataInitializer);
    }

    @Bean
    @ConditionalOnMissingBean(SQLTemplate.class)
    public SQLTemplate sqlTemplate(final JdbcTemplate jdbcTemplate) {
        return new JdbcSQLTemplate(jdbcTemplate);
    }

    @Bean
    public SqlDictI18nLoader sqlDictI18nLoader(final SQLTemplate sqlTemplate, final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        return new SqlDictI18nLoader(sqlTemplate, dictI18nLoaderCacheProvider, sqlDictI18nLoaderProperties);
    }
}