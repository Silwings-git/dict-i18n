package cn.silwings.dicti18n.loader.properties.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictI18nPropsProperties {

    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict-i18n/dict_*.properties
     */
    private String locationPattern = "classpath:dict-i18n/dict_*.properties";

    /**
     * Whether to ignore the dict case
     */
    private boolean ignoreCase = true;

}