package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneratePropertiesMojoTest {


    @Test
    void testGeneratePropertiesFilesFromEnumDict(@TempDir Path tempDir) throws Exception {
        final GeneratePropertiesMojo mojo = new GeneratePropertiesMojo();
        mojo.languages = Arrays.asList("zh", "en");

        // 模拟 Maven 项目结构
        final MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getBasedir()).thenReturn(new File("."));
        mojo.project = mockProject;

        // 指定 basePackages
        mojo.basePackages = Collections.singletonList("cn.silwings.dicti18n.plugin.generate");

        // 输出目录使用 JUnit 5 自动创建的临时目录
        mojo.outputDir = tempDir.toFile(); // 将 Path 转换为 File

        // 输出调试信息
        mojo.setLog(new SilentMojoLog());

        // 模拟扫描到的枚举类
        final Set<Class<? extends Dict>> classes = buildDictClasses();
        mojo.generate(classes, mojo.languages, mojo.outputDir, this.getClass().getClassLoader());
        mojo.generate(classes, mojo.languages, mojo.outputDir, this.getClass().getClassLoader());

        for (String lang : mojo.languages) {
            final File file = new File(mojo.outputDir, "dict_" + lang + ".properties");
            assertTrue(file.exists(), "File should exist: " + file.getName());

            final Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            }

            assertEquals("", props.getProperty("test.a"));
            assertEquals("", props.getProperty("test.b"));
        }
    }

    private static Set<Class<? extends Dict>> buildDictClasses() {
        final Set<Class<? extends Dict>> classes = new HashSet<>();
        classes.add(TestDictEnum.class);
        return classes;
    }
}