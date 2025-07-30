package cn.silwings.dicti18n.loader.declared;


import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.declared.config.DeclaredDictI18nLoaderProperties;
import cn.silwings.dicti18n.loader.declared.dict.DeclaredDict;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A DictI18nLoader implementation that loads all {@link Dict} implementations (enums or JavaBeans)
 * declared in the project at startup time and provides a simple language-agnostic dictionary lookup.
 * <p>
 * This loader is typically used for internal static dictionaries, such as enums with constant mappings.
 * It ignores the language parameter and returns a description based on:
 * <ul>
 *     <li>{@link DeclaredDict#getDesc()} if implemented</li>
 *     <li>Or a public method named <code>getDesc()</code> (via reflection)</li>
 *     <li>Or fallback to {@code Enum.name()}</li>
 * </ul>
 *
 * <p>
 * Warning: Since it loads at startup, this loader is not suitable for dynamic or multilingual scenarios.
 * </p>
 */
public class DeclaredDictI18nLoader implements DictI18nLoader, ApplicationContextAware, ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DeclaredDictI18nLoader.class);
    private final DictScanner dictScanner;
    private final DeclaredDictI18nLoaderProperties declaredDictI18nLoaderProperties;
    private ApplicationContext applicationContext;
    /**
     * Internal dictionary cache: key = dictName.code, value = Dict instance
     */
    private final Map<String, Dict> dictData = new ConcurrentHashMap<>();

    /**
     * Cache of getDesc() method for Dict classes (used only when not implementing DictWithDesc)
     */
    private final Map<Class<?>, Optional<Method>> descMethodCache = new ConcurrentHashMap<>();

    public DeclaredDictI18nLoader(final DictScanner dictScanner, final DeclaredDictI18nLoaderProperties declaredDictI18nLoaderProperties) {
        this.dictScanner = dictScanner;
        this.declaredDictI18nLoaderProperties = declaredDictI18nLoaderProperties;
    }

    @Override
    public String loaderName() {
        return "declared";
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Called by Spring Boot after application start-up.
     * Responsible for scanning and loading all Dict implementations.
     */
    @Override
    public void run(final ApplicationArguments args) {
        this.init();
    }

    /**
     * Initializes and loads all Dict instances found in scanned packages.
     */
    public void init() {
        // Determine base packages to scan
        final List<String> basePackages = this.getScanPackages();

        this.dictScanner
                .scan(basePackages)
                .forEach(clazz -> {
                    if (clazz.isEnum()) {
                        final Dict[] enumConstants = clazz.getEnumConstants();
                        if (ArrayUtils.isNotEmpty(enumConstants)) {
                            for (final Dict constant : enumConstants) {
                                this.dictData.put(this.processKey(String.format("%s.%s", constant.dictName(), constant.code())), constant);
                            }
                        }
                    } else {
                        try {
                            final Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            final Dict instance = (Dict) constructor.newInstance();
                            this.dictData.put(this.processKey(String.format("%s.%s", instance.dictName(), instance.code())), instance);
                        } catch (Exception e) {
                            throw new IllegalStateException("[DictI18n] JavaBean Dict class must have a no-arg constructor: " + clazz.getName(), e);
                        }
                    }
                });
        log.info("[DictI18n] {} Dict instances have been loaded.", this.dictData.size());
    }

    /**
     * Normalizes the key based on configuration (e.g., ignore case).
     */
    private String processKey(final String key) {
        return this.declaredDictI18nLoaderProperties.processKey(key);
    }

    /**
     * Gets the base packages to scan for Dict types.
     */
    private List<String> getScanPackages() {
        return this.declaredDictI18nLoaderProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.declaredDictI18nLoaderProperties.getScanPackages();
    }

    /**
     * Core dict text lookup method.
     * Ignores the {@code lang} parameter since this loader is not multilingual.
     *
     * @param lang    Unused language parameter
     * @param dictKey Full key in the form of dictName.code
     * @return Optional of text value (description)
     */
    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        final Dict dict = this.dictData.get(this.processKey(dictKey));
        if (null == dict) {
            return Optional.empty();
        }

        // The DictWithDesc interface is preferred
        if (dict instanceof DeclaredDict) {
            return Optional.ofNullable(((DeclaredDict) dict).getDesc());
        }

        // Reflection probe getDesc method (with caching)
        final Optional<Method> optionalMethod = this.descMethodCache.computeIfAbsent(dict.getClass(), clazz -> {
            try {
                final Method method = clazz.getMethod("getDesc");
                if (method.getReturnType().equals(String.class)) {
                    method.setAccessible(true);
                    return Optional.of(method);
                }
            } catch (NoSuchMethodException ignored) {
                // The getDesc method does not exist
            } catch (Exception e) {
                log.warn("[DictI18n] Unexpected error when scanning getDesc() of class {}: {}", clazz.getName(), e.getMessage());
            }
            return Optional.empty();
        });

        if (optionalMethod.isPresent()) {
            try {
                final String desc = (String) optionalMethod.get().invoke(dict);
                return Optional.ofNullable(desc);
            } catch (Exception e) {
                log.warn("[DictI18n] Failed to invoke getDesc() on {}: {}", dict.getClass().getName(), e.getMessage());
            }
        }

        // fallback: enum.name()
        if (dict instanceof Enum<?>) {
            return Optional.of(((Enum<?>) dict).name());
        }

        return Optional.empty();
    }
}