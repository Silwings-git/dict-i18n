package cn.silwings.dicti18n.loader.parser.strategy;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YmlDictParseStrategy implements DictFileParseStrategy {

    private final Logger log = LoggerFactory.getLogger(YmlDictParseStrategy.class);

    @Override
    public boolean supports(final Resource resource) {
        final String name = resource.getFilename();
        return null != name && name.endsWith(".yml");
    }

    @Override
    public List<DictInfo> parse(final Resource resource) {

        if (!resource.exists()) {
            this.handleMissingResource(resource);
            return Collections.emptyList();
        }

        try (InputStream inputStream = resource.getInputStream()) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> content = yaml.load(inputStream);
            final Map<String, String> flatMap = new HashMap<>();
            this.flatten("", content, flatMap);
            return this.convertToDictInfoList(flatMap);
        } catch (IOException e) {
            log.error("[DictI18n] Failed to read YML file: {}", resource.getDescription(), e);
            return Collections.emptyList();
        }
    }

    private List<DictInfo> convertToDictInfoList(final Map<String, String> flatMap) {
        return flatMap.entrySet()
                .stream()
                .map(entry -> new DictInfo(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private void handleMissingResource(final Resource resource) {
        if (resource instanceof ClassPathResource) {
            final String targetPath = ((ClassPathResource) resource).getPath();
            if (!ClassPathDictI18nLoader.LOCATION_PATTERNS.contains(targetPath)) {
                log.debug("[DictI18n] Resource not found: {}", resource.getDescription());
            }
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