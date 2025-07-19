package cn.silwings.dicti18n.loader.properties.config;

import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.properties")
public class PropsDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {

    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.properties
     */
    private List<String> locationPatterns = Collections.singletonList("classpath:dict_i18n/dict_*.properties");

    /**
     * Whether to ignore the dict case
     */
    private boolean ignoreCase = true;

}