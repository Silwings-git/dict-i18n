package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerateYmlMojoTest {

    private GenerateYmlMojo mojo;
    private File tempOutputDir;

    @BeforeEach
    void setUp() throws Exception {
        this.mojo = new GenerateYmlMojo();
        this.mojo.languages = Arrays.asList("zh", "en");

        // 模拟 Maven 项目结构
        final MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getBasedir()).thenReturn(new File("."));
        this.mojo.project = mockProject;

        // 指定 basePackages，避免 fallback 到自动包扫描
        this.mojo.basePackages = Collections.singletonList("cn.silwings.dicti18n.plugin.generate");

        // 输出目录使用临时文件夹
        this.tempOutputDir = Files.createTempDirectory("dict-test").toFile();
        this.mojo.outputDir = this.tempOutputDir;

        // 输出调试信息
        this.mojo.setLog(new SilentMojoLog());
    }

    @AfterEach
    void cleanup() {
        if (this.tempOutputDir != null && this.tempOutputDir.exists()) {
            deleteRecursive(this.tempOutputDir);
        }
    }

    @Test
    void testGeneratePropertiesFilesFromEnumDict() throws Exception {
        // 模拟只扫描到一个枚举类
        final Set<Class<? extends Dict>> classes = buildDictClasses();
        this.mojo.generate(classes, this.mojo.languages, this.mojo.outputDir);
        this.mojo.generate(classes, this.mojo.languages, this.mojo.outputDir);

        for (String lang : this.mojo.languages) {
            final File file = new File(this.tempOutputDir, "dict_" + lang + ".yml");
            assertTrue(file.exists(), "File should exist: " + file.getName());

            Map<String, Map<String, Object>> content;
            try (FileInputStream fis = new FileInputStream(file)) {
                content = new Yaml().load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            }

            assertEquals("", content.get("test").get("a"));
            assertEquals("", content.get("test").get("b"));
        }
    }

    private static Set<Class<? extends Dict>> buildDictClasses() {
        final Set<Class<? extends Dict>> classes = new HashSet<>();
        classes.add(TestDictEnum.class);
        return classes;
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }
}
