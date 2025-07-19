package cn.silwings.dicti18n.file.config;

import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.file")
public class FileDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {

    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict_i18n/dict_*.yml
     */
    private List<String> locationPatterns = Arrays.asList("classpath:dict_i18n/dict_*.yml", "classpath:dict_i18n/dict_*.properties");

}
