package cn.silwings.dicti18n.sorter;


import cn.silwings.dicti18n.loader.DictI18nLoader;

import java.util.List;

public interface DictLoaderSorter {
    /**
     * Get the loader that has been sorted
     */
    List<DictI18nLoader> getOrderedLoaders();
}
