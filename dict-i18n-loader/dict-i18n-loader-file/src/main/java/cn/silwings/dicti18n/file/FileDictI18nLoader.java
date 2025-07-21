package cn.silwings.dicti18n.file;

import cn.silwings.dicti18n.file.config.FileDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A universal Loader for dictionary resources from paths such as classpath, file, http(s), etc.
 */
public class FileDictI18nLoader implements ClassPathDictI18nLoader {

    private static final Logger log = LoggerFactory.getLogger(FileDictI18nLoader.class);
    private final FileDictI18nLoaderProperties fileDictI18nLoaderProperties;
    private final DictFileParser dictFileParser;
    private final Map<String, Map<String, String>> dictData;

    public FileDictI18nLoader(final FileDictI18nLoaderProperties fileDictI18nLoaderProperties, final DictFileParser dictFileParser) {
        this.fileDictI18nLoaderProperties = fileDictI18nLoaderProperties;
        this.dictFileParser = dictFileParser;
        this.dictData = new ConcurrentHashMap<>();
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @PostConstruct
    public void loadAll() {

        final Resource[] resources = this.loadResourcesFromPattern(this.fileDictI18nLoaderProperties.getLocationPatterns());

        for (Resource resource : resources) {
            final String lang = this.extractLangFromFilename(resource);
            if (null == lang) {
                continue;
            }
            final List<DictInfo> dictInfoList = this.dictFileParser.parse(resource);

            final Map<String, String> langDictMap = this.dictData.computeIfAbsent(lang.toLowerCase(), key -> new ConcurrentHashMap<>());
            dictInfoList.stream()
                    .filter(DictInfo::isValid)
                    .forEach(dictInfo -> langDictMap.put(this.fileDictI18nLoaderProperties.processKey(dictInfo.getDictKey()), dictInfo.getDictDesc()));
        }
    }

    @Override
    public String loaderName() {
        return "file";
    }

    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        if (null == lang || lang.isEmpty() || null == dictKey || dictKey.isEmpty()) {
            return Optional.empty();
        }
        final String langLowerCase = lang.toLowerCase();
        if (this.dictData.containsKey(langLowerCase)) {
            return Optional.ofNullable(this.dictData.get(langLowerCase).get(this.fileDictI18nLoaderProperties.processKey(dictKey)));
        }
        return Optional.empty();
    }
}