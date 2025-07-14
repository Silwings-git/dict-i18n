package cn.silwings.dicti18n.starter.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DictI18nCheckProperties {

    /**
     * Specifies whether to enable the uniqueness check of dictName.
     * If enabled, scanPackages must also be configured to determine which packages to scan for the Dict enumeration.
     */
    private boolean enableDictNameUniqueCheck = true;

    /**
     * A list of package paths to scan to find the {@link cn.silwings.dicti18n.dict.Dict} implementation class.
     */
    private List<String> scanPackages = new ArrayList<>();

}