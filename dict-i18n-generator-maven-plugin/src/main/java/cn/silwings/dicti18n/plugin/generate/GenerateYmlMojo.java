package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.plugin.YmlDictSyncTool;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "generate-yml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateYmlMojo extends AbstractDictGeneratorMojo {

    @Override
    void generate(final Set<Class<? extends Dict>> dictClassSet, final List<String> languages, final File outputDir) {
        final List<Dict[]> list = dictClassSet.stream()
                .map(dictClass -> {
                    try {
                        return dictClass.isEnum() ? dictClass.getEnumConstants() : new Dict[]{dictClass.getDeclaredConstructor().newInstance()};
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // TODO_Silwings: 2025/7/9 整合数据输出到文件
        final HashMap<String, Map<String, Map<String, String>>> languageMap = new HashMap<>();
        for (final String lang : languages) {
            final Map<String, Map<String, String>> dictMap = new HashMap<>();
            list.forEach(dictArray -> {
                final Map<String, String> itemMap = dictMap.computeIfAbsent(dictArray[0].dictName(), k -> new HashMap<>());
                for (final Dict dict : dictArray) {
                    itemMap.put(dict.dictName(), dict.code());
                }
            });
            languageMap.put(lang, dictMap);

        }
        YmlDictSyncTool.syncYmlDirectory(languageMap, outputDir);
    }
}