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

    /**
     * the prefix of the key in the dict cache
     */
    private String keyPrefix = "dict_i18n";

    /**
     * Decide whether to ignore string case based on the configuration
     *
     * @param key Enter a string
     * @return Processed string (lowercase is returned when case is ignored, otherwise it is returned as is)
     */
    public String processKey(final String key) {
        if (null == key || key.isEmpty()) {
            return key;
        }
        return this.isIgnoreCase() ? key.toLowerCase() : key;
    }

}