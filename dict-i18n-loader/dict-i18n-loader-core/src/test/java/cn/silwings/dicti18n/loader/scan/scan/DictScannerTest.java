package cn.silwings.dicti18n.loader.scan.scan;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DictScannerTest {

    @Test
    void shouldScanDictClassesFromPackage() {

        final DictScanner scanner = new DictScanner();
        final Collection<String> packages = Collections.singleton("cn.silwings.dicti18n.loader.scan");


        final Set<Class<Dict>> result = scanner.scan(packages);


        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(MockBeanDict.class));
        assertTrue(result.contains(MockEnumDict.class));
    }

    @Test
    void shouldFilterOutBlankPackageNames() {

        final DictScanner scanner = new DictScanner();
        final Collection<String> packages = Arrays.asList("", "cn.silwings.dicti18n.loader.scan", "  ", null);


        final Set<Class<Dict>> result = scanner.scan(packages);


        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Only TestDict1 and TestDict2 should be found
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptySetForNoPackages() {

        final DictScanner scanner = new DictScanner();
        final Collection<String> packages = Collections.emptyList();


        final Set<Class<Dict>> result = scanner.scan(packages);


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptySetForNonExistentPackage() {

        final DictScanner scanner = new DictScanner();
        final Collection<String> packages = Collections.singleton("nonexistent.package");


        final Set<Class<Dict>> result = scanner.scan(packages);


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

