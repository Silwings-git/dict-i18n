package cn.silwings.dicti18n.provider;

import java.util.Optional;

public interface DictI18nProvider {
    /**
     * Get internationalized copywriting
     */
    Optional<String> getText(String language, String defaultLanguage, String dictName, String code);
}