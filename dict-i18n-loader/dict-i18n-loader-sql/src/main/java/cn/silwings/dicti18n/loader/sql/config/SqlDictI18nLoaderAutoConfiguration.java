package cn.silwings.dicti18n.loader.sql.config;

import cn.silwings.dicti18n.loader.sql.SqlDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.cache.DictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.cache.GuavaDictI18nLoaderCacheProvider;
import cn.silwings.dicti18n.loader.sql.cache.NoCacheDictCacheProvider;
import cn.silwings.dicti18n.loader.sql.schema.SchemaInitRunner;
import cn.silwings.dicti18n.loader.sql.schema.SchemaInitializer;
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

    @Bean
    @ConditionalOnMissingBean(DictI18nLoaderCacheProvider.class)
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "true")
    public GuavaDictI18nLoaderCacheProvider guavaDictI18nLoaderCacheProvider(final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        return new GuavaDictI18nLoaderCacheProvider(sqlDictI18nLoaderProperties.getCache());
    }

    @Bean
    @ConditionalOnMissingBean(DictI18nLoaderCacheProvider.class)
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.cache.enabled", havingValue = "false", matchIfMissing = true)
    public NoCacheDictCacheProvider noCacheDictCacheProvider() {
        return new NoCacheDictCacheProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.schema.enabled", havingValue = "true")
    public SchemaInitializer schemaInitializer(final JdbcTemplate jdbcTemplate, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties) {
        return new SchemaInitializer(jdbcTemplate, sqlDictI18nLoaderProperties.getSchema());
    }

    @Bean
    @ConditionalOnProperty(name = "dict-i18n.loader.sql.schema.enabled", havingValue = "true")
    public SchemaInitRunner schemaInitRunner(final SchemaInitializer schemaInitializer) {
        return new SchemaInitRunner(schemaInitializer);
    }

    @Bean
    public SqlDictI18nLoader sqlDictI18nLoader(final JdbcTemplate jdbcTemplate, final DictI18nLoaderCacheProvider dictI18nLoaderCacheProvider) {
        return new SqlDictI18nLoader(jdbcTemplate, dictI18nLoaderCacheProvider);
    }
}