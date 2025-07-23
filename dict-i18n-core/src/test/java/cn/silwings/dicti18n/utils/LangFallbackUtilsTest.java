package cn.silwings.dicti18n.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LangFallbackUtilsTest {

    @Test
    public void testFallbackLangChainNull() {
        final List<String> result = LangFallbackUtils.fallbackLangChain((String) null);
        assertEquals(Collections.singletonList(""), result);
    }

    @Test
    public void testFallbackLangChainEmpty() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("");
        assertEquals(Collections.singletonList(""), result);
    }

    @Test
    public void testFallbackLangChainWhitespace() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("   ");
        assertEquals(Collections.singletonList(""), result);
    }

    @Test
    public void testFallbackLangChainSimpleLanguage() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("zh");
        assertEquals(Collections.singletonList("zh"), result);
    }

    @Test
    public void testFallbackLangChainComplexLanguage() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("zh-CN");
        assertEquals(Arrays.asList("zh-cn", "zh"), result);
    }

    @Test
    public void testFallbackLangChainComplexLanguageWithUnderscore() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("zh_CN");
        assertEquals(Arrays.asList("zh-cn", "zh"), result);
    }

    @Test
    public void testFallbackLangChainComplexLanguageWithMixedCase() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("Zh-Cn");
        assertEquals(Arrays.asList("zh-cn", "zh"), result);
    }

    @Test
    public void testFallbackLangChainComplexLanguageWithMultipleParts() {
        final List<String> result = LangFallbackUtils.fallbackLangChain("en-US-x-lvariant-POSIX");
        assertEquals(Arrays.asList("en-us-x-lvariant-posix", "en"), result);
    }

    @Test
    public void testFallbackLangChainLocaleNull() {
        final List<String> result = LangFallbackUtils.fallbackLangChain((Locale) null);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testFallbackLangChainLocaleSimple() {
        final List<String> result = LangFallbackUtils.fallbackLangChain(Locale.CHINESE);
        assertEquals(Collections.singletonList("zh"), result);
    }

    @Test
    public void testFallbackLangChainLocaleComplex() {
        final List<String> result = LangFallbackUtils.fallbackLangChain(Locale.CHINA);
        assertEquals(Arrays.asList("zh-cn", "zh"), result);
    }

    @Test
    public void testFallbackLangChainLocaleWithVariant() {
        final Locale locale = new Locale("en", "US", "POSIX");
        final List<String> result = LangFallbackUtils.fallbackLangChain(locale);
        assertEquals(Arrays.asList("en-us", "en"), result);
    }
}