package cn.silwings.dicti18n.loader;

import java.util.Optional;

public interface DictI18nLoader {

    /**
     * Loader name, should be unique
     */
    String loaderName();


    /**
     * Get translations based on language and key
     *
     * @param lang    language
     * @param dictKey dictionary key
     * @return translation
     */
    Optional<String> get(String lang, String dictKey);
}