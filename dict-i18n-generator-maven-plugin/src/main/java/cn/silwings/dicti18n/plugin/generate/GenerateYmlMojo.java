package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.plugin.utils.ConvertUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mojo(name = "generate-yml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateYmlMojo extends AbstractDictGeneratorMojo {

    @Override
    void generate(final List<Dict[]> dictsList, final List<String> languages, final File outputDir) throws MojoExecutionException {
        for (final String lang : languages) {
            // <dictName, <code, desc>>
            final String outFileName = String.format("dict_%s.yml", lang);
            final File outFile = outputDir.toPath().resolve(outFileName).toFile();
            final Map<String, Map<String, String>> newDictMap = this.parseDictListToMap(dictsList);
            final Map<String, Map<String, String>> oldDictMap = this.loadDictFromFile(outFile);
            final Map<String, Map<String, String>> targetDictMap = this.merge(oldDictMap, newDictMap);
            // TODO_Silwings: 2025/7/10 需要自然排序,目前顺序存在错误 
            this.write(targetDictMap, outFile);
        }
    }

    private void write(final Map<String, Map<String, String>> dictMap, final File outFile) throws MojoExecutionException {
        final DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setIndicatorIndent(1);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);

        Yaml yaml = new Yaml(new SafeConstructor(), new Representer(), options);

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), StandardCharsets.UTF_8)) {
            yaml.dump(dictMap, writer);
        } catch (IOException e) {
            this.getLog().error("Failed to write yaml: " + outFile.getName());
            throw new MojoExecutionException(e);
        }
    }

    private Map<String, Map<String, String>> merge(final Map<String, Map<String, String>> oldDictMap, final Map<String, Map<String, String>> newDictMap) {
        // 创建结果Map，先复制旧Map的所有内容
        final Map<String, Map<String, String>> mergedMap = new HashMap<>(oldDictMap);
        // 遍历新Map
        for (Map.Entry<String, Map<String, String>> newEntry : newDictMap.entrySet()) {
            final String outerKey = newEntry.getKey();
            final Map<String, String> newInnerMap = newEntry.getValue();
            // 如果旧Map中没有这个外部键，直接添加整个内部Map
            if (!mergedMap.containsKey(outerKey)) {
                mergedMap.put(outerKey, new TreeMap<>(newInnerMap));
            } else {
                // 如果旧Map中有这个外部键，合并内部Map
                final Map<String, String> oldInnerMap = mergedMap.get(outerKey);
                final Map<String, String> mergedInnerMap = new TreeMap<>(oldInnerMap);

                // 遍历新内部Map，添加旧内部Map中没有的条目
                for (Map.Entry<String, String> innerEntry : newInnerMap.entrySet()) {
                    if (!mergedInnerMap.containsKey(innerEntry.getKey())) {
                        mergedInnerMap.put(innerEntry.getKey(), innerEntry.getValue());
                    }
                }
                mergedMap.put(outerKey, mergedInnerMap);
            }
        }
        return mergedMap;
    }

    private Map<String, Map<String, String>> loadDictFromFile(final File outFile) {
        try (InputStream input = Files.newInputStream(outFile.toPath())) {
            final Yaml yaml = new Yaml(new SafeConstructor());
            final Object obj = yaml.load(input);
            if (obj instanceof Map<?, ?>) {
                final Map<String, Map<String, String>> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                    if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Map)) {
                        continue;
                    }

                    final Map<?, ?> innerMap = (Map<?, ?>) entry.getValue();
                    final Map<String, String> innerResult = new LinkedHashMap<>();
                    for (Map.Entry<?, ?> innerEntry : innerMap.entrySet()) {
                        if (innerEntry.getKey() instanceof String && innerEntry.getValue() instanceof String) {
                            innerResult.put((String) innerEntry.getKey(), (String) innerEntry.getValue());
                        }
                    }
                    result.put((String) entry.getKey(), innerResult);
                }
                return result;
            }
        } catch (Exception e) {
            this.getLog().warn("Failed to load yaml: " + outFile.getName());
        }
        return new LinkedHashMap<>();
    }

    private Map<String, Map<String, String>> parseDictListToMap(final List<Dict[]> dictArrayList) {
        final Map<String, Map<String, String>> dictMap = new HashMap<>();
        dictArrayList.forEach(dictArray -> {
            final Map<String, String> itemMap = dictMap.computeIfAbsent(dictArray[0].dictName(), k -> new TreeMap<>());
            for (final Dict dict : dictArray) {
                itemMap.put(ConvertUtil.getOrDefault(dict.code(), "_"), "");
            }
        });
        return dictMap;
    }

}