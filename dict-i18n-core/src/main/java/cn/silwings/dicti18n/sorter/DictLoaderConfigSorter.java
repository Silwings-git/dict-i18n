package cn.silwings.dicti18n.sorter;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DictLoaderConfigSorter implements DictLoaderSorter {

    private final DictI18nProperties properties;
    private final List<DictI18nLoader> allLoaders;

    public DictLoaderConfigSorter(final DictI18nProperties properties, final List<DictI18nLoader> allLoaders) {
        this.properties = properties;
        this.allLoaders = allLoaders;
    }

    @Override
    public List<DictI18nLoader> getOrderedLoaders() {
        // Map<beanName, loader>
        final Map<String, DictI18nLoader> loaderMap = this.allLoaders.stream().collect(Collectors.toMap(DictI18nLoader::loaderName, Function.identity()));

        // Sorting: Configure the order first, and then add others
        final List<DictI18nLoader> ordered = new ArrayList<>();

        // 1. The order of precedence specified in the configuration
        for (final String name : this.properties.getLoaderOrder()) {
            final DictI18nLoader loader = loaderMap.remove(name);
            if (null != loader) {
                ordered.add(loader);
            }
        }

        // 2. The remaining unconfigured loaders, sorted by class name
        final List<Map.Entry<String, DictI18nLoader>> remaining = new ArrayList<>(loaderMap.entrySet());
        remaining.sort(Map.Entry.comparingByKey());
        remaining.forEach(entry -> ordered.add(entry.getValue()));

        return ordered;
    }
}