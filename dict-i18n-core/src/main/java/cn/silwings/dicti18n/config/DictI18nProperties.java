package cn.silwings.dicti18n.config;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The basic configuration class of the dictionary internationalization module
 */
@Getter
@Setter
public class DictI18nProperties {

    /**
     * Specify the priority of the Loader name, for example: ["declared", "file", "redis", "sql"]
     */
    private List<String> loaderOrder = new ArrayList<>();

    /**
     * The maximum nesting depth when looking up a field
     */
    private int maxNestingDepth = 10;

    /**
     * Whether to return dictKey when description is empty
     */
    private boolean returnKeyIfEmpty = true;

    /**
     * The default language to use when a translation is not found
     */
    private String defaultLang = "";
}
