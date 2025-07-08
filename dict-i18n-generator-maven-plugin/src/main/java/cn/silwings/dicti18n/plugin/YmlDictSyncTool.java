package cn.silwings.dicti18n.plugin;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class YmlDictSyncTool {

    public static void syncYmlDirectory(Map<String, Map<String, Map<String, String>>> newData, final File outputDir) {
        if (!outputDir.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory: " + outputDir.getAbsolutePath());
        }

        newData.forEach((lang, data) -> {
            final String fileName = "dict_" + lang + ".yml";
            File file = new File(outputDir, fileName);
            if (!file.exists()) {
                createNewFile(file);
            }
            final Map<String, Map<String, String>> oldData = loadYaml(file);
            final Map<String, Map<String, String>> mergedData = mergeData(oldData, data);
            writeYamlSorted(file, mergedData);
        });
    }

    private static void createNewFile(final File file) {
        // 确保父目录存在
        final File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IllegalStateException("Unable to create a catalog: " + parentDir.getAbsolutePath());
        }

        // 创建空文件
        try {
            if (!file.createNewFile()) {
                throw new IllegalStateException("Unable to create file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a file: " + file.getAbsolutePath(), e);
        }
    }

    private static Map<String, Map<String, String>> loadYaml(File file) {
        try (InputStream input = Files.newInputStream(file.toPath())) {
            Yaml yaml = new Yaml(new SafeConstructor());
            Object obj = yaml.load(input);
            if (obj instanceof Map<?, ?>) {
                Map<String, Map<String, String>> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                    if (!(entry.getKey() instanceof String)) continue;
                    if (!(entry.getValue() instanceof Map)) continue;

                    Map<?, ?> innerMap = (Map<?, ?>) entry.getValue();
                    Map<String, String> innerResult = new LinkedHashMap<>();
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
            System.err.println("Failed to load yaml: " + file.getName());
        }
        return new LinkedHashMap<>();
    }

    private static Map<String, Map<String, String>> mergeData(Map<String, Map<String, String>> oldData,
                                                              Map<String, Map<String, String>> newData) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        // 处理所有新数据
        for (Map.Entry<String, Map<String, String>> entry : newData.entrySet()) {
            String dictKey = entry.getKey();
            Map<String, String> newItems = entry.getValue();

            Map<String, String> mergedItems = new LinkedHashMap<>();

            // 保留 new 中的 key，若在 old 中有保留原值
            for (String itemKey : newItems.keySet()) {
                mergedItems.put(itemKey, newItems.get(itemKey));
            }

            result.put(dictKey, mergedItems);
        }

        return result;
    }

    private static void writeYamlSorted(File file, Map<String, Map<String, String>> data) {
        // 排序 map
        Map<String, Map<String, String>> sortedOuter = new TreeMap<>();
        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            Map<String, String> inner = new TreeMap<>(entry.getValue()); // inner map 排序
            sortedOuter.put(entry.getKey(), inner);
        }

        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setIndicatorIndent(1);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);

        Yaml yaml = new Yaml(new SafeConstructor(), new Representer(), options);

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            yaml.dump(sortedOuter, writer);
        } catch (IOException e) {
            System.err.println("Failed to write yaml: " + file.getName());
        }
    }
}
