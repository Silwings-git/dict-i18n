package cn.silwings.dicti18n.starter.config;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Default implementation of {@link LanguageProvider} that obtains the current language
 * from Spring's {@link LocaleContextHolder}.
 * <p>
 * It returns the language tag (e.g., "en-US", "zh-CN") representing the current locale
 * associated with the executing thread.
 * </p>
 */
public class DefaultLanguageProvider implements LanguageProvider {
    @Override
    public String getCurrentLanguage() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }
}
