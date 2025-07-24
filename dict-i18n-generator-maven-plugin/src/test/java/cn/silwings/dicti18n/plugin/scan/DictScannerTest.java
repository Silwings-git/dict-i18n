package cn.silwings.dicti18n.plugin.scan;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 使用 Mockito 扩展替代 JUnit 4 的运行器
@ExtendWith(MockitoExtension.class)
public class DictScannerTest {

    @Mock
    private MavenProject mockProject;

    @Mock
    private Log mockLog;

    @Test
    public void shouldScanAndFindDictImplementation() throws Exception {
        // 准备测试环境
        final DictScanner scanner = new DictScanner();

        // 模拟项目编译路径
        final String classesDir = new File("target/test-classes").getAbsolutePath();
        when(mockProject.getCompileClasspathElements()).thenReturn(Collections.singletonList(classesDir));

        // 构建扫描上下文
        final ScanContext context = new ScanContext(
                mockProject,
                Collections.singletonList("cn.silwings.dicti18n.plugin.scan"),
                true,
                mockLog
        );

        // 执行扫描
        final Set<Class<? extends Dict>> result = scanner.scan(context);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream()
                .anyMatch(c -> c.getName().equals("cn.silwings.dicti18n.plugin.scan.DictScannerTest$TestDict")));
    }

    @Test
    public void testScanWithInvalidClasspath() throws Exception {
        // 模拟无效的类路径
        when(mockProject.getCompileClasspathElements()).thenReturn(Collections.singletonList("invalid/path"));

        final DictScanner scanner = new DictScanner();
        final ScanContext context = new ScanContext(mockProject, null, false, mockLog);

        // 执行扫描
        final Set<Class<? extends Dict>> scanned = scanner.scan(context);

        // 验证结果为空
        assertNotNull(scanned);
        assertEquals(0, scanned.size());
    }

    @Test
    public void testScanWithBasePackages() throws Exception {
        // 测试基础包过滤功能
        final List<String> classpathElements = Collections.singletonList("target/test-classes");
        when(mockProject.getCompileClasspathElements()).thenReturn(classpathElements);

        final DictScanner scanner = new DictScanner();
        final ScanContext context = new ScanContext(
                mockProject,
                Collections.singletonList("cn.silwings.dicti18n"),
                true,
                mockLog
        );

        final Set<Class<? extends Dict>> result = scanner.scan(context);

        // 验证扫描结果非空且调试日志被调用
        assertNotNull(result);
        verify(mockLog, atLeastOnce()).debug(anyString());
    }

    @Test
    public void testIsAbstract() {
        // 测试抽象类检测逻辑
        final DictScanner scanner = new DictScanner();

        // 具体实现类不应被判定为抽象
        assertFalse(scanner.isAbstract(TestDict.class));
        // 抽象类应被正确识别
        assertTrue(scanner.isAbstract(AbstractDict.class));
    }

    @Test
    public void testFindClassLocation() throws Exception {
        // 测试类文件位置查找
        final URL testClassesUrl = new File("target/test-classes").toURI().toURL();
        final List<URL> urls = Collections.singletonList(testClassesUrl);

        final DictScanner scanner = new DictScanner();
        final String location = scanner.findClassLocation(TestDict.class, urls);

        assertNotNull(location);
        assertNotEquals("Unknown location", location);
    }

    // 测试用的具体实现类
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

    // 测试用的抽象实现类
    public abstract static class AbstractDict implements Dict {
    }
}