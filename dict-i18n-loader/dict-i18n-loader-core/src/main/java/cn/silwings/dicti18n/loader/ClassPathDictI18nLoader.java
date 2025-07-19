package cn.silwings.dicti18n.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Arrays;
import java.util.List;

public interface ClassPathDictI18nLoader extends DictI18nLoader {

    Logger log = LoggerFactory.getLogger(ClassPathDictI18nLoader.class);
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Loading resources in single path mode (supports classpath, http).
     *
     * @param locationPattern Resource path, such as classpath*:dict_i18n/dict_*.yml
     * @return Array of matched resources, returns an empty array if failed
     */
    default Resource[] loadResourcesFromPattern(final String locationPattern) {
        if (null == locationPattern || locationPattern.trim().isEmpty()) {
            log.warn("Location pattern is null or empty.");
            return new Resource[0];
        }
        try {
            final Resource[] resources = resolver.getResources(locationPattern);
            log.debug("Loaded {} resource(s) from pattern: {}", resources.length, locationPattern);
            return resources;
        } catch (Throwable e) {
            log.warn("Failed to load resources from pattern '{}': {}", locationPattern, e.toString());
            return new Resource[0];
        }
    }

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

}