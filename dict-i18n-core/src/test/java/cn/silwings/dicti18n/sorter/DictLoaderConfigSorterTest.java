package cn.silwings.dicti18n.sorter;


import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DictLoaderConfigSorterTest {
    private DictI18nProperties properties;
    private List<DictI18nLoader> allLoaders;
    private DictLoaderConfigSorter sorter;

    @Before
    public void setUp() {
        this.properties = new DictI18nProperties();
        this.allLoaders = new ArrayList<>();
        this.sorter = new DictLoaderConfigSorter(this.properties, this.allLoaders);
    }

    @Test
    public void testNoLoaders() {
        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testNoConfiguredOrder() {
        final DictI18nLoader loader1 = mockLoader("loader1");
        final DictI18nLoader loader2 = mockLoader("loader2");
        final DictI18nLoader loader3 = mockLoader("loader3");
        this.allLoaders.addAll(Arrays.asList(loader1, loader2, loader3));

        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader1, loader2, loader3), result);
    }

    @Test
    public void testPartialConfiguredOrder() {
        this.properties.setLoaderOrder(Arrays.asList("loader2", "loader1"));

        final DictI18nLoader loader1 = mockLoader("loader1");
        final DictI18nLoader loader2 = mockLoader("loader2");
        final DictI18nLoader loader3 = mockLoader("loader3");
        this.allLoaders.addAll(Arrays.asList(loader1, loader2, loader3));

        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader2, loader1, loader3), result);
    }

    @Test
    public void testFullConfiguredOrder() {
        this.properties.setLoaderOrder(Arrays.asList("loader3", "loader1", "loader2"));

        final DictI18nLoader loader1 = mockLoader("loader1");
        final DictI18nLoader loader2 = mockLoader("loader2");
        final DictI18nLoader loader3 = mockLoader("loader3");
        this.allLoaders.addAll(Arrays.asList(loader1, loader2, loader3));

        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader3, loader1, loader2), result);
    }

    @Test
    public void testConfiguredOrderWithNonExistentLoader() {
        this.properties.setLoaderOrder(Arrays.asList("loader4", "loader2"));

        final DictI18nLoader loader1 = mockLoader("loader1");
        final DictI18nLoader loader2 = mockLoader("loader2");
        final DictI18nLoader loader3 = mockLoader("loader3");
        this.allLoaders.addAll(Arrays.asList(loader1, loader2, loader3));

        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader2, loader1, loader3), result);
    }

    @Test
    public void testRemainingLoadersSortedByOriginal() {
        this.properties.setLoaderOrder(Collections.singletonList("loader3"));

        final DictI18nLoader loader1 = mockLoader("loader1");
        final DictI18nLoader loader2 = mockLoader("loader2");
        final DictI18nLoader loader3 = mockLoader("loader3");
        this.allLoaders.addAll(Arrays.asList(loader2, loader1, loader3));

        final List<DictI18nLoader> result = sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader3, loader2, loader1), result);
    }

    @Test
    public void testLoaderNamesWithSpecialCharacters() {
        this.properties.setLoaderOrder(Arrays.asList("loader-2", "loader_1"));

        final DictI18nLoader loader1 = mockLoader("loader_1");
        final DictI18nLoader loader2 = mockLoader("loader-2");
        final DictI18nLoader loader3 = mockLoader("loader.3");
        this.allLoaders.addAll(Arrays.asList(loader1, loader2, loader3));

        final List<DictI18nLoader> result = this.sorter.getOrderedLoaders();
        assertEquals(Arrays.asList(loader2, loader1, loader3), result);
    }

    private static DictI18nLoader mockLoader(final String name) {
        return new DictI18nLoader() {
            @Override
            public String loaderName() {
                return name;
            }

            @Override
            public Optional<String> get(final String lang, final String dictKey) {
                return Optional.empty();
            }
        };
    }
}