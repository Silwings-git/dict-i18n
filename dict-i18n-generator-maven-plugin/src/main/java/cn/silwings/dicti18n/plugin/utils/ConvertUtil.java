package cn.silwings.dicti18n.plugin.utils;

import java.util.function.Supplier;

public class ConvertUtil {

    public static String getOrDefault(final String str, final Supplier<String> defaultValueSupplier) {
        if (null == str || str.trim().isEmpty()) {
            return defaultValueSupplier.get();
        }
        return str;
    }

    public static String getOrDefault(final String str, final String defaultValue) {
        if (null == str || str.trim().isEmpty()) {
            return defaultValue;
        }
        return str;
    }

}