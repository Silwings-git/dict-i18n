package cn.silwings.dicti18n.loader;

import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public interface ClassPathDictI18nLoader extends DictI18nLoader {

    List<String> LOCATION_PATTERNS = Arrays.asList("dict_i18n/dict_*.yml", "dict_i18n/dict_*.properties", "dict_i18n/dict.yml", "dict_i18n/dict.properties");

    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Loading resources in single path mode (supports classpath, http).
     *
     * @param locationPattern Resource path, such as classpath*:dict_i18n/dict_*.yml
     * @return Array of matched resources, returns an empty array if failed
     */
    default Resource[] loadResourcesFromPattern(final String locationPattern) {
        if (null == locationPattern || locationPattern.trim().isEmpty()) {
            this.getLog().warn("[DictI18n] Location pattern is null or empty.");
            return new Resource[0];
        }
        try {
            final Resource[] resources = resolver.getResources(locationPattern);
            this.getLog().debug("[DictI18n] Loaded {} resource(s) from pattern: {}", resources.length, locationPattern);
            return resources;
        } catch (Throwable e) {
            if (!(e instanceof FileNotFoundException && LOCATION_PATTERNS.contains(locationPattern))) {
                this.getLog().warn("[DictI18n] Failed to load resources from pattern '{}': {}", locationPattern, e.toString());
            }
            return new Resource[0];
        }
    }

    Logger getLog();

    /**
     * Load resources in bulk under multiple path patterns.。
     *
     * @param locationPatterns multiple path patterns
     * @return An array of all matched resources
     */
    default Resource[] loadResourcesFromPattern(final List<String> locationPatterns) {
        if (null == locationPatterns || locationPatterns.isEmpty()) {
            return new Resource[0];
        }
        return locationPatterns.stream()
                .map(this::loadResourcesFromPattern)
                .flatMap(Arrays::stream)
                .toArray(Resource[]::new);
    }

    default String extractLangFromFilename(final Resource resource) {

        final String filename = resource.getFilename();

        if (null == filename || !filename.contains(".") || (!filename.startsWith("dict_") && !filename.startsWith("dict."))) {
            return null;
        }

        final String raw = filename.substring(0, filename.lastIndexOf("."));

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

        return lang.isEmpty() ? null : lang.toLowerCase();
    }

}