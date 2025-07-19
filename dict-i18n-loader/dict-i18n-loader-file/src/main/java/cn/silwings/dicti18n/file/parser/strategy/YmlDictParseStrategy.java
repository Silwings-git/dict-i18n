package cn.silwings.dicti18n.file.parser.strategy;

import cn.silwings.dicti18n.file.parser.DictInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YmlDictParseStrategy implements DictFileParseStrategy {

    private static final Logger log = LoggerFactory.getLogger(YmlDictParseStrategy.class);

    @Override
    public boolean supports(final Resource resource) {
        final String name = resource.getFilename();
        return null != name && name.endsWith(".yml");
    }

    @Override
    public List<DictInfo> parse(final Resource resource) {
        try {
            final Yaml yaml = new Yaml();
            final Map<String, Object> content = yaml.load(resource.getInputStream());
            final Map<String, String> flatMap = new HashMap<>();
            this.flatten("", content, flatMap);
            return flatMap.entrySet()
                    .stream()
                    .map(entry -> new DictInfo(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to read the YML file: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private void flatten(final String prefix, final Map<String, Object> source, final Map<String, String> target) {
        if (null == source) {
            return;
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            final String dictKey = null == prefix || prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            final Object dictDesc = entry.getValue();
            if (dictDesc instanceof Map<?, ?>) {
                flatten(dictKey, (Map<String, Object>) dictDesc, target);
            } else if (null != dictKey && null != dictDesc) {
                target.put(dictKey, dictDesc.toString());
            }
        }
    }
}