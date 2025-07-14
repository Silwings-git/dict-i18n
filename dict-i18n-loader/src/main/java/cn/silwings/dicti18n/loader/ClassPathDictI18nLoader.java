package cn.silwings.dicti18n.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public interface ClassPathDictI18nLoader extends DictI18nLoader {

    Logger log = LoggerFactory.getLogger(ClassPathDictI18nLoader.class);
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    default Resource[] loadResourcesFromPattern(final String locationPattern) {
        if (null == locationPattern || locationPattern.trim().isEmpty()) {
            log.warn("Location pattern is null or empty.");
            return new Resource[0];
        }
        try {
            return resolver.getResources(locationPattern);
        } catch (Throwable e) {
            log.warn("Failed to load yml: {}", e, e);
            return new Resource[0];
        }
    }

}