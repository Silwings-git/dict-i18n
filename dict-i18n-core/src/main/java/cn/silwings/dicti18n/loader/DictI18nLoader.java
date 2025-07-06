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
     * @param lang 语言
     * @param key  字典键
     * @return 译文
     */
    Optional<String> get(String lang, String key);
}