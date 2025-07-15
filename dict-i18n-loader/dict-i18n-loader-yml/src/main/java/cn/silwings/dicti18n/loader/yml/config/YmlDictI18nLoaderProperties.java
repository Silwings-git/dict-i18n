package cn.silwings.dicti18n.loader.yml.config;


import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.yml")
public class YmlDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {

    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private String locationPattern = "classpath:dict_i18n/dict_*.yml";


}