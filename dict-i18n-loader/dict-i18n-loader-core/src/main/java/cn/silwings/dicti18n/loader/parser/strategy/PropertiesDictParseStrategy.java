package cn.silwings.dicti18n.loader.parser.strategy;

import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesDictParseStrategy implements DictFileParseStrategy {

    private final Logger log = LoggerFactory.getLogger(PropertiesDictParseStrategy.class);

    @Override
    public boolean supports(final Resource resource) {
        final String name = resource.getFilename();
        return null != name && name.endsWith(".properties");
    }

    @Override
    public List<DictInfo> parse(final Resource resource) {

        if (!resource.exists()) {
            this.handleMissingResource(resource);
            return Collections.emptyList();
        }

        return this.loadProperties(resource)
                .map(this::convertToDictInfoList)
                .orElse(Collections.emptyList());
    }

    private List<DictInfo> convertToDictInfoList(final Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> new DictInfo(entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.toList());
    }

    private Optional<Properties> loadProperties(final Resource resource) {
        try (InputStreamReader input = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            final Properties properties = new Properties();
            properties.load(input);
            return Optional.of(properties);
        } catch (IOException e) {
            log.error("[DictI18n] Failed to load properties: {}", resource.getDescription(), e);
            return Optional.empty();
        }
    }

    private void handleMissingResource(final Resource resource) {
        if (resource instanceof ClassPathResource) {
            final String targetPath = ((ClassPathResource) resource).getPath();
            if (!ClassPathDictI18nLoader.LOCATION_PATTERNS.contains(targetPath)) {
                log.warn("[DictI18n] Failed to load properties: {}", resource.getDescription());
            }
        }
    }

}

