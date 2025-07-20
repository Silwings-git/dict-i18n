package cn.silwings.dicti18n.loader.parser.strategy;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesDictParseStrategy implements DictFileParseStrategy {

    private static final Logger log = LoggerFactory.getLogger(PropertiesDictParseStrategy.class);

    @Override
    public boolean supports(final Resource resource) {
        final String name = resource.getFilename();
        return null != name && name.endsWith(".properties");
    }

    @Override
    public List<DictInfo> parse(final Resource resource) {

        final Properties properties = new Properties();
        try (InputStreamReader input = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            properties.load(input);
        } catch (IOException e) {
            log.warn("Failed to load properties: {}", resource.getFilename(), e);
        }

        final List<DictInfo> dictList = new ArrayList<>();
        properties.forEach((dictKey, dictDesc) -> {
            if (null != dictKey && null != dictDesc) {
                dictList.add(new DictInfo(dictKey.toString(), dictDesc.toString()));
            }
        });
        return dictList;
    }

}

