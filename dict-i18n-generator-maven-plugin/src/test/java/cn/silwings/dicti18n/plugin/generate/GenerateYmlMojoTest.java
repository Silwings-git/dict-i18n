package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerateYmlMojoTest {

    /**
     * 测试从枚举字典生成YML文件的功能
     * 使用 @TempDir 注解自动管理临时目录，替代手动创建和删除
     *
     * @param tempDir JUnit 5 自动创建的临时目录路径，测试结束后自动清理
     */
    @Test
    void testGeneratePropertiesFilesFromEnumDict(@TempDir Path tempDir) throws Exception {
        // 初始化测试目标对象
        final GenerateYmlMojo mojo = new GenerateYmlMojo();
        mojo.languages = Arrays.asList("zh", "en");

        // 模拟 Maven 项目基础目录
        final MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getBasedir()).thenReturn(new File("."));
        mojo.project = mockProject;

        // 指定扫描的基础包，避免自动扫描带来的不可控性
        mojo.basePackages = Collections.singletonList("cn.silwings.dicti18n.plugin.generate");

        // 设置输出目录为临时目录（自动创建，测试后自动删除）
        mojo.outputDir = tempDir.toFile();

        // 设置日志输出（静默模式，避免测试时打印冗余日志）
        mojo.setLog(new SilentMojoLog());

        // 模拟扫描到的字典枚举类
        final Set<Class<? extends Dict>> classes = buildDictClasses();
        // 执行生成逻辑（调用两次以验证幂等性，确保重复生成不会出问题）
        mojo.generate(classes, mojo.languages, mojo.outputDir);
        mojo.generate(classes, mojo.languages, mojo.outputDir);

        // 验证每种语言的YML文件是否正确生成
        for (String lang : mojo.languages) {
            final File ymlFile = new File(mojo.outputDir, "dict_" + lang + ".yml");
            // 断言文件存在
            assertTrue(ymlFile.exists(), "生成的YML文件不存在: " + ymlFile.getName());

            // 读取YML文件内容并验证
            Map<String, Map<String, Object>> ymlContent;
            try (FileInputStream fis = new FileInputStream(ymlFile)) {
                // 使用UTF-8编码读取，避免中文乱码
                ymlContent = new Yaml().load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            }

            // 验证字典键对应的value是否正确（根据实际业务场景调整断言值）
            assertEquals("", ymlContent.get("test").get("a"));
            assertEquals("", ymlContent.get("test").get("b"));
        }
    }

    /**
     * 构建测试用的字典类集合
     * 此处添加需要测试的字典枚举类
     *
     * @return 字典类集合
     */
    private static Set<Class<? extends Dict>> buildDictClasses() {
        final Set<Class<? extends Dict>> classes = new HashSet<>();
        classes.add(TestDictEnum.class); // 添加测试用的枚举字典类
        return classes;
    }
}