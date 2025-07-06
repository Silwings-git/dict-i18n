package cn.silwings.dicti18n.starter.check;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.starter.config.DictI18nCheckProperties;
import cn.silwings.dicti18n.starter.scan.DictScanner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Checks for uniqueness of {@link Dict#dictName()} across all Dict implementations at application startup.
 * <p>
 * This class scans the configured packages (or Spring autoconfigured packages by default),
 * finds all classes implementing the {@link Dict} interface, and ensures that no two Dict classes
 * declare the same (case-insensitive) {@code dictName}.
 * </p>
 * <p>
 * This validation helps avoid dictionary collisions in internationalization use cases.
 * </p>
 */
public class DictNameUniqueChecker implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(DictNameUniqueChecker.class);

    private ApplicationContext applicationContext;
    private final DictI18nCheckProperties dictI18nCheckProperties;

    public DictNameUniqueChecker(final DictI18nCheckProperties dictI18nCheckProperties) {
        this.dictI18nCheckProperties = dictI18nCheckProperties;
    }

    /**
     * Spring will inject the application context automatically.
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Invoked after all dependencies are injected.
     * Scans for all {@link Dict} implementations and checks for {@code dictName} uniqueness.
     */
    @PostConstruct
    public void checkDictNames() {

        if (!this.dictI18nCheckProperties.isEnableDictNameUniqueCheck()) {
            return;
        }

        // Determine base packages to scan
        final List<String> basePackages = this.dictI18nCheckProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.dictI18nCheckProperties.getScanPackages();
        final Set<Class<Dict>> dictClasses = new DictScanner().scan(new HashSet<>(basePackages));

        final Map<String, Class<?>> dictNameMap = new HashMap<>();

        for (Class<Dict> dictClass : dictClasses) {
            try {
                final String dictName = this.resolveDictName(dictClass);
                checkDictNameNotBlank(dictName, dictClass);

                final String lowerCaseDictName = dictName.toLowerCase();

                if (dictNameMap.containsKey(lowerCaseDictName)) {
                    final Class<?> previous = dictNameMap.get(lowerCaseDictName);
                    throw new ApplicationContextException(
                            String.format("Duplicate dictName '%s' found in both %s and %s",
                                    dictName, previous.getName(), dictClass.getName())
                    );
                }

                dictNameMap.put(lowerCaseDictName, dictClass);

            } catch (Exception e) {
                throw new ApplicationContextException("Dict validation failed for class: " + dictClass.getName(), e);
            }
        }

        log.info("DictName uniqueness validation passed. Total valid Dicts: {}", dictNameMap.size());
    }

    /**
     * Resolves the {@code dictName} from a Dict class.
     * If it's an enum, all enum constants must return the same dictName.
     * If it's a JavaBean, the default no-arg constructor is used.
     *
     * @param clazz the class implementing {@link Dict}
     * @return the resolved dict name
     */
    private String resolveDictName(final Class<?> clazz) {
        if (clazz.isEnum()) {
            final Object[] enumConstants = clazz.getEnumConstants();
            if (null == enumConstants || enumConstants.length == 0) {
                throw new IllegalStateException("Enum " + clazz.getName() + " has no constants");
            }

            final String firstDictName = ((Dict) enumConstants[0]).dictName();
            for (Object constant : enumConstants) {
                final String currentName = ((Dict) constant).dictName();
                if (!Objects.equals(firstDictName, currentName)) {
                    throw new ApplicationContextException(
                            "All enum constants of " + clazz.getName() + " must have the same dictName. Found: '"
                            + firstDictName + "' and '" + currentName + "'");
                }
            }
            return firstDictName;
        } else {
            try {
                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                final Dict instance = (Dict) constructor.newInstance();
                return instance.dictName();
            } catch (Exception e) {
                throw new IllegalStateException("JavaBean Dict class must have a no-arg constructor: " + clazz.getName(), e);
            }
        }
    }

    /**
     * Throws an exception if the provided dict name is null or blank.
     */
    private static void checkDictNameNotBlank(final String dictName, final Class<?> dictClass) {
        if (StringUtils.isBlank(dictName)) {
            log.debug("Validating dictName='{}' from class '{}'", dictName, dictClass.getName());
            throw new ApplicationContextException("dictName cannot be null or blank. Invalid Dict: " + dictClass.getName());
        }
    }

}
