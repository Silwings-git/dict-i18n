package cn.silwings.dicti18n.starter.sorter;

import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.sorter.DictLoaderSorter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link DictLoaderSorter} that sorts
 * {@link DictI18nLoader} beans based on Spring's {@link AnnotationAwareOrderComparator}.
 * <p>
 * This sorter respects the @Order annotation or Ordered interface implemented
 * by the loaders to determine their order.
 */
public class SpringDictLoaderSorter implements DictLoaderSorter {

    /**
     * All available DictI18nLoader instances to be sorted.
     */
    private final List<DictI18nLoader> allLoaders;

    /**
     * Constructor injecting all loaders to be sorted.
     *
     * @param dictI18nLoaders the list of all {@link DictI18nLoader} beans
     */
    public SpringDictLoaderSorter(final List<DictI18nLoader> dictI18nLoaders) {
        this.allLoaders = dictI18nLoaders;
    }

    /**
     * Returns the loaders sorted according to Spring's order annotations.
     *
     * @return a sorted list of {@link DictI18nLoader} instances
     */
    @Override
    public List<DictI18nLoader> getOrderedLoaders() {
        final List<DictI18nLoader> remaining = new ArrayList<>(this.allLoaders);
        AnnotationAwareOrderComparator.sort(remaining);
        return remaining;
    }
}