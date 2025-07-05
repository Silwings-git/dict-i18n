package cn.silwings.dicti18n.plugin;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractDictGeneratorMojo extends AbstractMojo {

    @Parameter(property = "basePackages", required = true)
    protected List<String> basePackages;

    @Parameter(property = "languages", required = true)
    protected List<String> languages;

    @Parameter(property = "outputDir", defaultValue = "${project.build.directory}/dict")
    protected File outputDir;

    protected Set<Class<Dict>> scanDicts() {
//        return new DictScanner().scan(new HashSet<>(basePackages));
        return new HashSet<>();
    }


}