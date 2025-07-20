package cn.silwings.dicti18n.file;

import cn.silwings.dicti18n.file.config.FileDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
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

    private final FileDictI18nLoaderProperties fileDictI18nLoaderProperties;
    private final DictFileParser dictFileParser;
    private final Map<String, Map<String, String>> dictData;

    public FileDictI18nLoader(final FileDictI18nLoaderProperties fileDictI18nLoaderProperties, final DictFileParser dictFileParser) {
        this.fileDictI18nLoaderProperties = fileDictI18nLoaderProperties;
        this.dictFileParser = dictFileParser;
        this.dictData = new ConcurrentHashMap<>();
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
                    .forEach(dictInfo -> langDictMap.put(this.processKey(dictInfo.getDictKey()), dictInfo.getDictDesc()));
        }
    }

    public String extractLangFromFilename(final Resource resource) {

        final String filename = resource.getFilename();

        if (null == filename || !filename.contains(".") || !filename.startsWith("dict_")) {
            return null;
        }

        final String raw = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();

        if ("dict".equals(raw)) {
            return CompositeDictI18nProvider.FALLBACK_LOCALE_KEY;
        }

        // Extract the language section
        int idx = raw.lastIndexOf('_');
        if (idx == -1) {
            idx = raw.lastIndexOf('-');
        }
        if (idx == -1) {
            idx = raw.lastIndexOf('.');
        }

        final String lang = idx == -1 ? raw : raw.substring(idx + 1);

        return lang.isEmpty() ? null : lang;
    }

    /**
     * Decide whether to ignore string case based on the configuration
     *
     * @param key Enter a string
     * @return Processed string (lowercase is returned when case is ignored, otherwise it is returned as is)
     */
    public String processKey(final String key) {
        if (null == key || key.isEmpty()) {
            return key;
        }
        return this.fileDictI18nLoaderProperties.isIgnoreCase() ? key.toLowerCase() : key;
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
            return Optional.ofNullable(this.dictData.get(langLowerCase).get(this.processKey(dictKey)));
        }
        return Optional.empty();
    }
}