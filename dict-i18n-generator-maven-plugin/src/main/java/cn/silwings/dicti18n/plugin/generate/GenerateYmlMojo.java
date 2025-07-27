package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mojo(name = "yml", defaultPhase = LifecyclePhase.COMPILE)
public class GenerateYmlMojo extends AbstractDictGeneratorMojo {

    @Override
    void generate(final List<Dict[]> dictsList, final List<String> languages, final File outputDir) throws MojoExecutionException {
        for (final String lang : languages) {
            // <dictName, <code, desc>>
            final String outFileName = String.format("dict_%s.yml", lang);
            final File outFile = outputDir.toPath().resolve(outFileName).toFile();
            final Map<String, Object> newDictMap = this.parseDictListToMap(dictsList);
            final Map<String, Object> oldDictMap = this.loadDictFromFile(outFile);
            final Map<String, Object> targetDictMap = this.merge(oldDictMap, newDictMap);
            this.write(targetDictMap, outFile);
        }
    }

    private void write(final Map<String, Object> dictMap, final File outFile) throws MojoExecutionException {
        final DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setIndicatorIndent(1);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);

        final Yaml yaml = new Yaml(new SafeConstructor(), new Representer(), options);

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), StandardCharsets.UTF_8)) {
            yaml.dump(dictMap, writer);
        } catch (IOException e) {
            this.getLog().error("[DictI18n] Failed to write yaml: " + outFile.getName());
            throw new MojoExecutionException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> merge(final Map<String, Object> oldMap, final Map<String, Object> newMap) {
        final Map<String, Object> merged = new TreeMap<>(oldMap);

        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            final String key = entry.getKey();
            final Object newValue = entry.getValue();

            if (oldMap.containsKey(key)) {
                final Object oldValue = oldMap.get(key);

                if (newValue instanceof Map && oldValue instanceof Map) {
                    merged.put(key, this.merge((Map<String, Object>) oldValue, (Map<String, Object>) newValue));
                } else {
                    merged.put(key, oldValue);
                }
            } else {
                merged.put(key, newValue);
            }
        }

        return merged;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadDictFromFile(final File outFile) {
        try (InputStream input = Files.newInputStream(outFile.toPath())) {
            final Yaml yaml = new Yaml(new SafeConstructor());
            final Object obj = yaml.load(input);
            if (obj instanceof Map<?, ?>) {
                return (Map<String, Object>) obj;
            }
        } catch (Exception e) {
            this.getLog().debug("[DictI18n] Failed to load yaml: " + outFile.getName());
        }
        return new TreeMap<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseDictListToMap(final List<Dict[]> dictArrayList) {
        final Map<String, Object> dictMap = new TreeMap<>();

        dictArrayList.forEach(dictArray -> {
            final String dictName = dictArray[0].dictName();
            final String[] keys = dictName.split("\\.");

            Map<String, Object> currentMap = dictMap;

            for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i];
                currentMap = (Map<String, Object>) currentMap.computeIfAbsent(key, k -> new TreeMap<>());
            }

            final String lastKey = keys[keys.length - 1];
            final Map<String, String> itemMap = (Map<String, String>) currentMap.computeIfAbsent(lastKey, k -> new TreeMap<>());

            for (final Dict dict : dictArray) {
                itemMap.put(StringUtils.defaultIfBlank(dict.code(), "_"), "");
            }
        });

        return dictMap;
    }

}