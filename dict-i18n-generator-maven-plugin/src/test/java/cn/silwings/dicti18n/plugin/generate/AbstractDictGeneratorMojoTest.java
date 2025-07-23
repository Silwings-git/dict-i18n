package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDictGeneratorMojoTest {

    @Spy
    private TestDictGeneratorMojo mojo;

    @Mock
    private Build mockBuild;

    @Mock
    private MavenProject mockProject;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        this.mojo.project = this.mockProject;
        this.mojo.basePackages = Collections.singletonList("cn.silwings.dicti18n.plugin.generate");
        this.mojo.languages = Arrays.asList("en", "zh");
        this.mojo.verbose = false;
        when(this.mockProject.getBuild()).thenReturn(this.mockBuild);
        when(this.mockProject.getBasedir()).thenReturn(this.tempFolder.newFolder("project-root"));
    }

    private void createJavaFiles(File srcDir) throws IOException {
        // 创建模拟的Java文件
        final File pkgDir = new File(srcDir, "cn/silwings/dicti18n/plugin/generate");
        assertTrue(pkgDir.mkdirs());

        Files.createFile(new File(pkgDir, "TestClass.java").toPath());
        Files.createFile(new File(srcDir, "RootClass.java").toPath());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindPackagesInDirectory() throws Exception {
        // 测试在目录中查找包
        // Test finding packages in directory
        final File srcDir = this.tempFolder.newFolder("src", "main", "java");
        createJavaFiles(srcDir);

        final Method findPackagesInDirectory = this.mojo.getClass().getSuperclass().getSuperclass().getDeclaredMethod("findPackagesInDirectory", File.class, File.class);
        findPackagesInDirectory.setAccessible(true);
        final List<String> packages = (List<String>) findPackagesInDirectory.invoke(mojo, srcDir, srcDir);

        // 忽略默认包,也就是RootClass.java
        assertEquals(1, packages.size());
        assertTrue(packages.contains("cn.silwings.dicti18n.plugin.generate"));
    }

    @Test
    public void testCalculatePackageName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 测试计算包名
        // Test calculating package name
        final File rootDir = new File("/project/src/main/java");
        final File currentDir = new File("/project/src/main/java/cn/silwings/dicti18n/plugin/generate");

        final Method calculatePackageName = this.mojo.getClass().getSuperclass().getSuperclass().getDeclaredMethod("calculatePackageName", File.class, File.class);
        calculatePackageName.setAccessible(true);

        final String packageName = calculatePackageName.invoke(mojo, rootDir, currentDir).toString();
        assertEquals("cn.silwings.dicti18n.plugin.generate", packageName);
    }

    @Test
    public void testInitOutputDirDefault() throws Exception {
        // 测试默认输出目录初始化
        // Test default output directory initialization
        this.mojo.outputDir = null;
        final File projectRoot = this.mockProject.getBasedir();

        this.mojo.initOutputDir();

        assertNotNull(this.mojo.outputDir);
        assertEquals(
                new File(projectRoot, "src/main/resources/dict_i18n").getAbsolutePath(),
                this.mojo.outputDir.getAbsolutePath()
        );
    }

    @Test
    public void testInitOutputDirUserSpecified() throws Exception {
        // 测试用户指定的输出目录
        // Test user-specified output directory
        final File customDir = this.tempFolder.newFolder("custom-output");
        this.mojo.outputDir = customDir;

        this.mojo.initOutputDir();

        assertSame(customDir, this.mojo.outputDir);
    }

    @Test(expected = MojoExecutionException.class)
    public void testInitOutputDirCreationFailed() throws Exception {
        // 测试输出目录创建失败
        // Test output directory creation failure
        this.mojo.outputDir = new File("0~1234se!@#$%^&*()_+");
        this.mojo.initOutputDir();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateDictSetProcessing() throws Exception {
        // 测试处理Dict集合
        // Test processing Dict set
        final Set<Class<? extends Dict>> dictClassSet = new HashSet<>();
        dictClassSet.add(MockEnumDict.class);
        dictClassSet.add(MockClassDict.class);

        this.mojo.generate(dictClassSet, this.mojo.languages, this.mojo.outputDir);

        // 验证抽象方法被调用
        // Verify abstract method was called
        verify(this.mojo, times(1)).generate(anyList(), eq(this.mojo.languages), eq(this.mojo.outputDir));
    }

    @Test
    public void testExecuteWithDefaultBasePackages() throws Exception {
        // 测试使用默认基础包执行
        // Test execution with default base packages
        this.mojo.basePackages = new ArrayList<>();
        when(this.mockProject.getCompileClasspathElements())
                .thenReturn(Collections.singletonList(this.tempFolder.newFolder("target").getAbsolutePath()));

        final File srcDir = this.tempFolder.newFolder("src", "main", "java");
        when(this.mockProject.getBuild().getSourceDirectory()).thenReturn(srcDir.getAbsolutePath());
        createJavaFiles(srcDir);

        this.mojo.execute();

        // 验证已正确设置基础包
        // Verify base packages were set correctly
        assertFalse(this.mojo.basePackages.isEmpty());
        assertEquals(1, this.mojo.basePackages.size());
        assertTrue(this.mojo.basePackages.contains("cn.silwings.dicti18n.plugin.generate"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithSpecifiedBasePackages() throws Exception {
        // 测试使用指定基础包执行
        // Test execution with specified base packages
        this.mojo.verbose = true;
        when(this.mockProject.getCompileClasspathElements())
                .thenReturn(Collections.singletonList(this.tempFolder.newFolder("target").getAbsolutePath()));

        this.mojo.execute();

        // 验证生成方法被调用
        // Verify generate method was called
        verify(this.mojo, times(1)).generate(anySet(), eq(this.mojo.languages), any());
    }

    @Test
    public void testExecuteWithMissingSourceDirectory() throws Exception {
        // 测试执行时源码目录缺失
        // Test execution with missing source directory
        this.mojo.basePackages = new ArrayList<>();
        when(this.mockProject.getBuild().getSourceDirectory()).thenReturn("/invalid/path");

        this.mojo.execute();
    }

    // 测试用实现
    // Test implementations

    public static class TestDictGeneratorMojo extends AbstractDictGeneratorMojo {
        @Override
        public void generate(List<Dict[]> dictList, List<String> languages, File outputDir) {
            // 测试实现方法
        }
    }

    public enum MockEnumDict implements Dict {
        TEST1, TEST2;

        @Override
        public String dictName() {
            return "mock";
        }

        @Override
        public String code() {
            return this.name();
        }

    }

    public static class MockClassDict implements Dict {
        @Override
        public String dictName() {
            return "classDict";
        }

        @Override
        public String code() {
            return "123";
        }
    }
}
