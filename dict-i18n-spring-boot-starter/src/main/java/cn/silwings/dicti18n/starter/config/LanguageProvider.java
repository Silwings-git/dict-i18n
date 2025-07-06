package cn.silwings.dicti18n.starter.config;

/**
 * Provides the current language of the context
 */
public interface LanguageProvider {
    /**
     * Get the current context language
     */
    String getCurrentLanguage();
}
