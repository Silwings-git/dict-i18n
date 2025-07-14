package cn.silwings.dicti18n.config;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DictI18nStaticProperties {

    /**
     * A list of base package paths for automatically scanning the DICT interface implementation class
     */
    private List<String> scanPackages = new ArrayList<>();

    /**
     * Whether to ignore the dict case
     */
    private boolean ignoreCase = true;

}