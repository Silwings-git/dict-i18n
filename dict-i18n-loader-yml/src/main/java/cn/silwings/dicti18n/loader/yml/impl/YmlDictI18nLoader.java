package cn.silwings.dicti18n.loader.yml.impl;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.yml.config.DictI18nYmlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class YmlDictI18nLoader implements ClassPathDictI18nLoader {

    private static final Logger log = LoggerFactory.getLogger(YmlDictI18nLoader.class);
    private static final String FILE_SUFFIX = ".yml";

    private final DictI18nYmlProperties dictI18nYmlProperties;

    private final Map<String, Map<String, String>> dictData = new ConcurrentHashMap<>();

    public YmlDictI18nLoader(final DictI18nYmlProperties dictI18nYmlProperties) {
        this.dictI18nYmlProperties = dictI18nYmlProperties;
    }

    @PostConstruct
    public void loadAll() {

        final Resource[] resources = this.loadResourcesFromPattern(this.dictI18nYmlProperties.getLocationPattern());

        for (final Resource resource : resources) {
            final String filename = resource.getFilename();
            if (null == filename || !filename.endsWith(FILE_SUFFIX)) {
                continue;
            }

            final String lang = this.extractLangFromFilename(filename);
            if (null != lang) {
                try {
                    final Yaml yaml = new Yaml();
                    final Map<String, Object> content = yaml.load(resource.getInputStream());
                    final Map<String, String> flatMap = new ConcurrentHashMap<>();
                    this.flatten("", content, flatMap);
                    this.dictData.putIfAbsent(lang.toLowerCase(), flatMap);
                } catch (IOException e) {
                    log.error("Failed to read the YML file: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Decide whether to ignore string case based on the configuration
     *
     * @param input Enter a string
     * @return Processed string (lowercase is returned when case is ignored, otherwise it is returned as is)
     */
    public String processString(final String input) {
        if (null == input || input.isEmpty()) {
            return input;
        }
        return this.dictI18nYmlProperties.isIgnoreCase() ? input.toLowerCase() : input;
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

    @SuppressWarnings("unchecked")
    private void flatten(final String prefix, final Map<String, Object> source, final Map<String, String> target) {
        if (null == source) {
            return;
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = null == prefix || prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                flatten(key, (Map<String, Object>) value, target);
            } else {
                target.put(this.processString(key), String.valueOf(value));
            }
        }
    }

    @Override
    public String loaderName() {
        return "yml";
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        if (null == lang || lang.isEmpty() || null == key || key.isEmpty()) {
            return Optional.empty();
        }
        final String langLowerCase = lang.toLowerCase();
        if (this.dictData.containsKey(langLowerCase)) {
            return Optional.ofNullable(this.dictData.get(langLowerCase).get(this.processString(key)));
        }
        return Optional.empty();
    }
}