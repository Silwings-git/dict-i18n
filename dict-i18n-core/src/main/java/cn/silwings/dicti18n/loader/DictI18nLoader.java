package cn.silwings.dicti18n.loader;

import java.util.Optional;

/**
 * A unified interface for dictionary internationalization loader, defining the core methods that all loaders must implement.
 *
 * <p>This interface serves as the foundation of the platinized architecture for loaders in the dict-i18n framework.
 * All built-in or custom loaders must implement this interface to ensure the system can uniformly schedule dictionary loading
 * logic from different data sources.</p>
 *
 * <p>Core responsibilities：</p>
 * <ul>
 *   <li>Define the unique identifier of the loader ({@link #loaderName()}), used for configuration and priority control</li>
 *   <li>Provide a standard method to query translation results based on language and dictionary key ({@link #get(String, String)})</li>
 * </ul>
 *
 * <p>The implementation class needs to realize specific loading logic based on its own characteristics (such as files,
 * databases, Redis, etc. data sources), and can enhance functionality by combining with extension components like caching
 * and data parsing. The system will invoke each loader through this interface's unified contract in the order of configured
 * priorities, achieving collaboration among multiple data sources.</p>
 */
public interface DictI18nLoader {

    /**
     * Loader name, should be unique
     */
    String loaderName();


    /**
     * Get translations based on language and key
     *
     * @param lang    lowercase language
     * @param dictKey dictionary key
     * @return translation
     */
    Optional<String> get(String lang, String dictKey);
}