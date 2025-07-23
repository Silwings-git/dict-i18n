package cn.silwings.dicti18n.plugin.scan;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DictScannerTest {

    @Mock
    private MavenProject mockProject;

    @Mock
    private Log mockLog;

    @Test
    public void shouldScanAndFindDictImplementation() throws Exception {
        // Arrange
        final DictScanner scanner = new DictScanner();

        // Mock project
        final String classesDir = new File("target/test-classes").getAbsolutePath();
        when(this.mockProject.getCompileClasspathElements()).thenReturn(Collections.singletonList(classesDir));

        // Build ScanContext
        final ScanContext context = new ScanContext(this.mockProject, Collections.singletonList("cn.silwings.dicti18n.plugin.scan"), true, this.mockLog);

        // Act
        final Set<Class<? extends Dict>> result = scanner.scan(context);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("cn.silwings.dicti18n.plugin.scan.DictScannerTest$TestDict")));
    }

    @Test
    public void testScanWithInvalidClasspath() throws Exception {
        when(this.mockProject.getCompileClasspathElements()).thenReturn(Collections.singletonList("invalid/path"));

        final DictScanner scanner = new DictScanner();
        final ScanContext context = new ScanContext(this.mockProject, null, false, this.mockLog);

        final Set<Class<? extends Dict>> scanned = scanner.scan(context);
        assertNotNull(scanned);
        assertEquals(0, scanned.size());
    }

    @Test
    public void testScanWithBasePackages() throws Exception {
        // 测试使用基础包过滤扫描
        // Test scanning with base package filter
        final List<String> classpathElements = Collections.singletonList("target/test-classes");

        when(this.mockProject.getCompileClasspathElements()).thenReturn(classpathElements);

        final DictScanner scanner = new DictScanner();
        final ScanContext context = new ScanContext(this.mockProject,
                Collections.singletonList("cn.silwings.dicti18n"),
                true, this.mockLog);

        final Set<Class<? extends Dict>> result = scanner.scan(context);

        assertNotNull(result);
        verify(this.mockLog, atLeastOnce()).debug(anyString());
    }

    @Test
    public void testIsAbstract() {
        // 测试抽象类检测方法
        // Test abstract class detection method
        final DictScanner scanner = new DictScanner();

        assertFalse(scanner.isAbstract(TestDict.class));
        assertTrue(scanner.isAbstract(AbstractDict.class));
    }

    @Test
    public void testFindClassLocation() throws Exception {
        // 测试查找类文件位置方法
        // Test find class location method
        final URL testClassesUrl = new File("target/test-classes").toURI().toURL();
        final List<URL> urls = Collections.singletonList(testClassesUrl);

        final DictScanner scanner = new DictScanner();
        final String location = scanner.findClassLocation(TestDict.class, urls);

        assertNotNull(location);
        assertNotEquals("Unknown location", location);
    }

    // Test classes for scanning
    public static class TestDict implements Dict {
        @Override
        public String dictName() {
            return "test_dict";
        }

        @Override
        public String code() {
            return "1";
        }
    }

    public abstract static class AbstractDict implements Dict {
    }
}
