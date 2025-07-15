package cn.silwings.dicti18n.loader.config;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractDictI18nLoaderProperties {

    /**
     * Whether to enable the dict loader
     */
    private boolean enabled = true;

    /**
     * Whether to ignore the dict case
     */
    private boolean ignoreCase = true;

}