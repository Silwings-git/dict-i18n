package cn.silwings.dicti18n.loader.yml.config;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictI18nYmlProperties {

    /**
     * Specify the resource path, and support Spring Resource path formats such as classpath: file:.
     * Default path: classpath:dict/dict_*.yml
     */
    private String locationPattern = "classpath:dict/dict_*.yml";

}