package cn.silwings.dicti18n.loader.sql.init.data;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.sql.SqlDictI18nLoader;
import cn.silwings.dicti18n.loader.sql.config.SqlDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.sql.init.schema.DictI18nSchemaInitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DictI18nSqlDataInitRunner implements ApplicationRunner, Ordered {
    private static final Logger log = LoggerFactory.getLogger(DictI18nSqlDataInitRunner.class);
    // Ensure this class is executed after DictI18nSchemaInitRunner
    public static final int ORDER = DictI18nSchemaInitRunner.ORDER + 1;

    private final SqlDictI18nLoader sqlDictI18nLoader;
    private final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties;
    private final DictFileParser dictFileParser;
    private final DictI18nSqlDataInitializer dictI18NSqlDataInitializer;

    public DictI18nSqlDataInitRunner(final SqlDictI18nLoader sqlDictI18nLoader, final SqlDictI18nLoaderProperties sqlDictI18nLoaderProperties, final DictFileParser dictFileParser, final DictI18nSqlDataInitializer dictI18NSqlDataInitializer) {
        this.sqlDictI18nLoader = sqlDictI18nLoader;
        this.sqlDictI18nLoaderProperties = sqlDictI18nLoaderProperties;
        this.dictFileParser = dictFileParser;
        this.dictI18NSqlDataInitializer = dictI18NSqlDataInitializer;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void run(final ApplicationArguments args) {
        log.info("[DictI18n] Starting to preload data into SQL database.");

        final Resource[] resources = this.sqlDictI18nLoader.loadResourcesFromPattern(this.sqlDictI18nLoaderProperties.getLocationPatterns());

        final Map<String, List<DictInfo>> langDictMap = new HashMap<>();
        for (Resource resource : resources) {
            final String lang = this.sqlDictI18nLoader.extractLangFromFilename(resource);
            if (null == lang) {
                continue;
            }
            final List<DictInfo> dictInfoList = this.dictFileParser.parse(resource)
                    .stream()
                    .filter(DictInfo::isValid)
                    .collect(Collectors.toList());
            if (!dictInfoList.isEmpty()) {
                langDictMap.put(lang, dictInfoList);
            }
        }

        try {
            this.dictI18NSqlDataInitializer.initialize(langDictMap, this.sqlDictI18nLoaderProperties.getPreload().getPreloadMode());
            log.info("[DictI18n] Completed preloaded data into SQL database.");
        } catch (Exception e) {
            if (this.sqlDictI18nLoaderProperties.getPreload().isFailFast()) {
                log.error("[DictI18n] Failed to preload data into SQL database.", e);
                throw e;
            } else {
                log.warn("[DictI18n] Failed to preload data into SQL database, continuing without preloading.", e);
            }
        }
    }
}