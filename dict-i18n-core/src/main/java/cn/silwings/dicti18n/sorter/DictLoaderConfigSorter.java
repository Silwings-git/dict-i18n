package cn.silwings.dicti18n.sorter;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        final List<String> configOrder = this.properties.getLoaderOrder();

        // No custom order, return in the original order directly.
        if (null == configOrder || configOrder.isEmpty()) {
            return new ArrayList<>(this.allLoaders);
        }

        final Map<String, DictI18nLoader> loaderMap = this.allLoaders.stream()
                .collect(Collectors.toMap(DictI18nLoader::loaderName, Function.identity()));

        final List<DictI18nLoader> ordered = new ArrayList<>();
        final Set<String> used = new HashSet<>();

        // Add the loader in the configuration sequence
        for (final String name : configOrder) {
            final DictI18nLoader loader = loaderMap.get(name);
            if (null != loader) {
                ordered.add(loader);
                used.add(name);
            }
        }

        // Add the remaining loaders in their original order
        for (final DictI18nLoader loader : this.allLoaders) {
            if (!used.contains(loader.loaderName())) {
                ordered.add(loader);
            }
        }

        return ordered;
    }
}