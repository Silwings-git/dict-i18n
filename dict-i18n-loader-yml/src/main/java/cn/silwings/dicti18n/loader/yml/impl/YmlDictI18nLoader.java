package cn.silwings.dicti18n.loader.yml.impl;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.yml.config.DictI18nYmlProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class YmlDictI18nLoader implements DictI18nLoader {

    private static final String FILE_SUFFIX = ".yml";

    private final DictI18nYmlProperties dictI18nYmlProperties;

    private final Map<String, Map<String, String>> dictData = new ConcurrentHashMap<>();

    public YmlDictI18nLoader(final DictI18nYmlProperties dictI18nYmlProperties) {
        this.dictI18nYmlProperties = dictI18nYmlProperties;
    }

    @PostConstruct
    public void loadAll() throws IOException {
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final Resource[] resources = resolver.getResources(this.dictI18nYmlProperties.getLocationPattern());

        for (final Resource resource : resources) {
            final String filename = resource.getFilename();
            if (null == filename || !filename.endsWith(FILE_SUFFIX)) {
                continue;
            }

            final String lang = this.extractLangFromFilename(filename);
            if (null != lang) {
                final Yaml yaml = new Yaml();
                final Map<String, Object> content = yaml.load(resource.getInputStream());
                final Map<String, String> flatMap = new HashMap<>();
                this.flatten("", content, flatMap);

                this.dictData.computeIfAbsent(this.processString(lang), __ -> new ConcurrentHashMap<>()).putAll(flatMap);
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
        final String langLowerCase = lang.toLowerCase();
        if (this.dictData.containsKey(langLowerCase)) {
            return Optional.ofNullable(this.dictData.get(langLowerCase).get(key));
        }
        return Optional.empty();
    }
}