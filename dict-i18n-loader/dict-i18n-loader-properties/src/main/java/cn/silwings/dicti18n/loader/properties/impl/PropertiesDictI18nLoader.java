package cn.silwings.dicti18n.loader.properties.impl;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.properties.config.DictI18nPropsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesDictI18nLoader implements ClassPathDictI18nLoader {

    private static final Logger log = LoggerFactory.getLogger(PropertiesDictI18nLoader.class);
    private static final String FILE_SUFFIX = ".properties";

    private final DictI18nPropsProperties dictI18nPropsProperties;

    private final Map<String, Map<String, String>> dictMap;

    public PropertiesDictI18nLoader(final DictI18nPropsProperties dictI18nPropsProperties) {
        this.dictI18nPropsProperties = dictI18nPropsProperties;
        this.dictMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void loadAll() {

        final Resource[] resources = this.loadResourcesFromPattern(this.dictI18nPropsProperties.getLocationPattern());

        for (final Resource resource : resources) {
            final String filename = resource.getFilename();
            if (null == filename || !filename.endsWith(FILE_SUFFIX)) {
                continue;
            }

            final String lang = this.extractLangFromFilename(filename);
            if (null != lang) {
                final Properties properties = new Properties();
                try (InputStreamReader input = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    properties.load(input);
                } catch (IOException e) {
                    log.warn("Failed to load properties: {}", resource.getFilename(), e);
                }
                final Map<String, String> dictMap = new ConcurrentHashMap<>();
                properties.forEach((k, v) -> dictMap.put(this.processKey((String) k), (String) v));
                this.dictMap.put(lang.toLowerCase(), dictMap);
            }
        }
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
        return this.dictI18nPropsProperties.isIgnoreCase() ? key.toLowerCase() : key;
    }

    private String extractLangFromFilename(String filename) {
        // Supports dict_en.yml / dict-en.yml / dict.en.yml etc.
        final String raw = filename.replace(FILE_SUFFIX, "").toLowerCase();

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


    @Override
    public String loaderName() {
        return "properties";
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        if (null == lang || lang.isEmpty() || null == key || key.isEmpty()) {
            return Optional.empty();
        }
        final String lowerCaseLang = lang.toLowerCase();
        if (this.dictMap.containsKey(lowerCaseLang)) {
            return Optional.ofNullable(this.dictMap.get(lowerCaseLang).get(this.processKey(key)));
        }
        return Optional.empty();
    }
}