package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractDictGeneratorMojoTest {

    @Spy
    private TestDictGeneratorMojo mojo;

    @Mock
    private Build mockBuild;

    @Mock
    private MavenProject mockProject;

    @BeforeEach
    void setUp() throws Exception {
        try (final AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
            this.mojo.project = this.mockProject;
            this.mojo.basePackages = Collections.singletonList("cn.silwings.dicti18n.plugin.generate");
            this.mojo.languages = Arrays.asList("en", "zh");
            this.mojo.verbose = false;
            when(this.mockProject.getBuild()).thenReturn(this.mockBuild);
        }
    }

    private void createJavaFiles(File srcDir) throws IOException {
        final File pkgDir = new File(srcDir, "cn/silwings/dicti18n/plugin/generate");
        assertTrue(pkgDir.mkdirs());

        Files.createFile(new File(pkgDir, "TestClass.java").toPath());
        Files.createFile(new File(srcDir, "RootClass.java").toPath());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindPackagesInDirectory(@TempDir File tempDir) throws Exception {
        final File srcDir = new File(tempDir, "src/main/java");
        createJavaFiles(srcDir);

        final Method findPackagesInDirectory = getDeclaredMethod("findPackagesInDirectory", File.class, File.class);
        final List<String> packages = (List<String>) findPackagesInDirectory.invoke(mojo, srcDir, srcDir);

        assertEquals(1, packages.size());
        assertTrue(packages.contains("cn.silwings.dicti18n.plugin.generate"));
    }

    @Test
    void testCalculatePackageName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final File rootDir = new File("/project/src/main/java");
        final File currentDir = new File("/project/src/main/java/cn/silwings/dicti18n/plugin/generate");

        final Method calculatePackageName = getDeclaredMethod("calculatePackageName", File.class, File.class);
        final String packageName = calculatePackageName.invoke(mojo, rootDir, currentDir).toString();

        assertEquals("cn.silwings.dicti18n.plugin.generate", packageName);
    }

    @Test
    void testInitOutputDirDefault(@TempDir File projectRoot) throws Exception {
        this.mojo.outputDir = null;
        when(this.mockProject.getBasedir()).thenReturn(projectRoot);

        this.mojo.initOutputDir();

        assertNotNull(this.mojo.outputDir);
        assertEquals(
                new File(projectRoot, "src/main/resources/dict_i18n").getAbsolutePath(),
                this.mojo.outputDir.getAbsolutePath()
        );
    }

    @Test
    void testInitOutputDirUserSpecified(@TempDir File customDir) throws Exception {
        this.mojo.outputDir = customDir;
        this.mojo.initOutputDir();

        assertSame(customDir, this.mojo.outputDir);
    }

    @Test
    void testGenerateDictSetProcessing() throws Exception {
        final Set<Class<? extends Dict>> dictClassSet = new HashSet<>();
        dictClassSet.add(MockEnumDict.class);
        dictClassSet.add(MockClassDict.class);

        this.mojo.generate(dictClassSet, this.mojo.languages, mock(File.class));

        verify(this.mojo, times(1)).generate(anyList(), eq(this.mojo.languages), any());
    }

    @Test
    void testExecuteWithDefaultBasePackages(@TempDir File tempDir) throws Exception {
        this.mojo.basePackages = new ArrayList<>();
        final File targetDir = new File(tempDir, "target");
        final File srcDir = new File(tempDir, "src/main/java");
        createJavaFiles(srcDir);

        when(this.mockProject.getCompileClasspathElements())
                .thenReturn(Collections.singletonList(targetDir.getAbsolutePath()));
        when(this.mockProject.getBuild().getSourceDirectory()).thenReturn(srcDir.getAbsolutePath());

        this.mojo.execute();

        assertFalse(this.mojo.basePackages.isEmpty());
        assertEquals(1, this.mojo.basePackages.size());
        assertTrue(this.mojo.basePackages.contains("cn.silwings.dicti18n.plugin.generate"));
    }

    @Test
    void testExecuteWithSpecifiedBasePackages(@TempDir File tempDir) throws Exception {
        this.mojo.verbose = true;
        final File targetDir = new File(tempDir, "target");

        when(this.mockProject.getCompileClasspathElements())
                .thenReturn(Collections.singletonList(targetDir.getAbsolutePath()));

        this.mojo.execute();

        verify(this.mojo, times(1)).generate(anySet(), eq(this.mojo.languages), any());
    }

    @Test
    void testExecuteWithMissingSourceDirectory() throws Exception {
        this.mojo.basePackages = new ArrayList<>();
        when(this.mockProject.getBuild().getSourceDirectory()).thenReturn("/invalid/path");

        this.mojo.execute();
    }

    private Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = mojo.getClass().getSuperclass().getSuperclass().getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }
}