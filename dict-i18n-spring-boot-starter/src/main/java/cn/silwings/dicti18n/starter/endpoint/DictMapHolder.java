package cn.silwings.dicti18n.starter.endpoint;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import cn.silwings.dicti18n.starter.config.DictI18nStarterProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dictionary mapper is responsible for scanning, initializing, and storing all dictionary data.
 * As a Spring component, it automatically scans for enum classes in the specified package path at application startup,
 * registers enum classes that implement the {@link Dict} interface as dictionaries, and maintains a mapping between
 * dictionary names and dictionary entries.
 *
 * <p>
 * This class provides thread-safe dictionary access methods, supporting retrieval of corresponding dictionary entry
 * arrays by dictionary name.
 * Can be shared by multiple EndpointHandler instances, avoiding repeated scanning and initialization of dictionary data.
 * <p>
 *
 * @see Dict
 * @see DictScanner
 * @see DictI18nStarterProperties
 */
public class DictMapHolder implements ApplicationContextAware, ApplicationRunner {

    private static final Dict[] EMPTY_DICT_ARRAY = new Dict[0];
    private ApplicationContext applicationContext;
    private final DictScanner dictScanner;
    private final DictI18nStarterProperties dictI18nStarterProperties;

    // A mapping of dictionary names to arrays of dictionary entries,
    private final Map<String, Dict[]> dictMap;

    public DictMapHolder(final DictScanner dictScanner, final DictI18nStarterProperties dictI18nStarterProperties) {
        this.dictScanner = dictScanner;
        this.dictI18nStarterProperties = dictI18nStarterProperties;
        this.dictMap = new ConcurrentHashMap<>();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Perform initialization when the application starts, scan and register all dictionaries.
     */
    @Override
    public void run(final ApplicationArguments args) {
        this.init();
    }

    /**
     * Initialization method, scans all enumeration classes under the specified package and registers them as dictionaries
     */
    public void init() {
        this.dictScanner
                .scan(this.getScanPackages())
                .stream()
                .filter(Class::isEnum)
                .forEach(clazz -> {
                    final Dict[] enumConstants = clazz.getEnumConstants();
                    if (ArrayUtils.isNotEmpty(enumConstants)) {
                        this.dictMap.put(enumConstants[0].dictName(), enumConstants);
                    }
                });
    }

    /**
     * Get the list of scan package paths
     * If the scan package is configured, use the configured value; otherwise, use the auto-configured package path.
     */
    private List<String> getScanPackages() {
        return this.dictI18nStarterProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.dictI18nStarterProperties.getScanPackages();
    }

    /**
     * Get the list of dictionary items based on the dictionary name.
     *
     * @param dictName Dictionary name
     * @return A list of dictionary items, return an empty array if it does not exist
     */
    public Dict[] getDictItems(final String dictName) {
        return this.dictMap.getOrDefault(dictName, EMPTY_DICT_ARRAY);
    }

    /**
     * Get the set of all dictionary names
     *
     * @return collection of dictionary names
     */
    public Set<String> getAllDictNames() {
        return this.dictMap.keySet();
    }
}
