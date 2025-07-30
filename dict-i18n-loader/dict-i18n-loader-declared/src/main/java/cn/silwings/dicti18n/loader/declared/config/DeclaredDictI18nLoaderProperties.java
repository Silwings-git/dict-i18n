package cn.silwings.dicti18n.loader.declared.config;


import cn.silwings.dicti18n.loader.config.AbstractDictI18nLoaderProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.loader.declared")
public class DeclaredDictI18nLoaderProperties extends AbstractDictI18nLoaderProperties {

    /**
     * A list of base package paths for automatically scanning the DICT interface implementation class
     */
    private List<String> scanPackages = new ArrayList<>();

}