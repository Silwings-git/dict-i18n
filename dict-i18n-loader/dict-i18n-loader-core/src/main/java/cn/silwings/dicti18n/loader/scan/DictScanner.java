package cn.silwings.dicti18n.loader.scan;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for scanning dictionary-related classes implementing the {@link Dict} interface
 * within specified base packages.
 */
public class DictScanner {

    private static final Logger log = LoggerFactory.getLogger(DictScanner.class);

    /**
     * Scans the given packages and returns all classes that implement the {@link Dict} interface.
     *
     * @param packages the set of base packages to scan; packages should not be blank
     * @return a set of classes implementing the {@link Dict} interface found within the specified packages
     */
    public Set<Class<? extends Dict>> scan(final Collection<String> packages) {

        final DictClassPathScanningCandidateComponentProvider provider =
                new DictClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AssignableTypeFilter(Dict.class));

        return packages
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(provider::findCandidateComponents)
                .flatMap(Collection::stream)
                .map(BeanDefinition::getBeanClassName)
                .map(this::classForName)
                .collect(Collectors.toSet());
    }

    /**
     * Load the class by name and cast to Class<Dict>.
     * If the class is not found, logs a warning and returns null.
     *
     * @param className the fully qualified class name to load
     * @return the Class object representing the class, or null if not found
     */
    @SuppressWarnings("unchecked")
    private Class<Dict> classForName(final String className) {
        try {
            return (Class<Dict>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.warn("[DictI18n] Class not found: {}", e.getMessage(), e);
            return null;
        }
    }

}