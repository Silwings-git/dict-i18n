package cn.silwings.dicti18n.starter.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DictI18nResponseEnhancerProperties {

    /**
     * Whether to enable global response enhancements
     */
    private boolean enabled = true;

    /**
     * The package to which the included return class belongs (return type)
     */
    private List<String> includePackages = new ArrayList<>();

    /**
     * Specifies which annotation classes or methods do not need to be enhanced
     */
    private List<String> excludeAnnotations = new ArrayList<>();
}
