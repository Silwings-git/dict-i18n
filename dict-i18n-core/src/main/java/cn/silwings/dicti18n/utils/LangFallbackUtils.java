package cn.silwings.dicti18n.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Provide language degradation policies, such as:
 * input zh-CN => [zh-cn, zh]
 * input en-US => [en-us, en]
 * input zh => [zh]
 */
public final class LangFallbackUtils {

    private LangFallbackUtils() {
    }

    /**
     * Given the lang string, a list of demotion candidates is returned.
     *
     * @param lang Language identifiers，e.g zh-CN、en-US、zh
     */
    public static List<String> fallbackLangChain(String lang) {
        if (StringUtils.isBlank(lang)) {
            return Collections.singletonList("");
        }

        // Replace the underscore with an underscore to unify the format
        lang = lang.trim().toLowerCase().replace('_', '-');

        final List<String> fallbackList = new ArrayList<>();
        fallbackList.add(lang);

        // If you are a compound language (e.g. en-us)
        if (lang.contains("-")) {
            String baseLang = lang.substring(0, lang.indexOf('-'));
            fallbackList.add(baseLang);
        }

        return fallbackList;
    }

    /**
     * Locale to fallback chain
     */
    public static List<String> fallbackLangChain(Locale locale) {
        if (null == locale) {
            return Collections.emptyList();
        }

        final String language = locale.getLanguage();
        final String country = locale.getCountry();

        final List<String> fallbackList = new ArrayList<>();

        if (!language.isEmpty() && !country.isEmpty()) {
            fallbackList.add((language + "-" + country).toLowerCase());
        }

        if (!language.isEmpty()) {
            fallbackList.add(language.toLowerCase());
        }

        return fallbackList;
    }
}
