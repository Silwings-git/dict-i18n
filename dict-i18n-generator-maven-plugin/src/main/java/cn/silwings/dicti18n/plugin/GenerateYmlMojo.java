package cn.silwings.dicti18n.plugin;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "generate-yml", defaultPhase = LifecyclePhase.COMPILE)
public class GenerateYmlMojo extends AbstractDictGeneratorMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "basePackages", required = false)
    private List<String> basePackages;

    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("开始查找Dict接口的实现类...");

        try {
            // 获取编译后的类路径
            List<String> classpathElements = project.getCompileClasspathElements();
            List<URL> urls = new ArrayList<>();

            for (String element : classpathElements) {
                try {
                    urls.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    getLog().error("转换类路径元素失败: " + element, e);
                }
            }

            // 创建类加载器
            ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());

            // 获取Reflections配置
            ConfigurationBuilder config = new ConfigurationBuilder()
                    .setClassLoaders(new ClassLoader[]{classLoader})
                    .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated);

            // 添加扫描路径
            if (basePackages != null && !basePackages.isEmpty()) {
                config.setUrls(urls.stream()
                        .flatMap(url -> basePackages.stream()
                                .map(pkg -> url.toString() + "/" + pkg.replace('.', '/')))
                        .map(path -> {
                            try {
                                return new URL(path);
                            } catch (MalformedURLException e) {
                                getLog().warn("无效的基础包路径: " + path, e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            } else {
                config.setUrls(urls);
            }

            // 扫描类
            Reflections reflections = new Reflections(config);

            // 查找所有实现类
            Set<Class<? extends Dict>> allImplementations = reflections.getSubTypesOf(Dict.class);

            // 过滤掉抽象类和接口
            Set<Class<?>> concreteImplementations = allImplementations.stream()
                    .filter(cls -> !cls.isInterface() && !isAbstract(cls))
                    .collect(Collectors.toSet());

            // 输出结果
            getLog().info("找到 " + concreteImplementations.size() + " 个Dict接口的实现类:");
            for (Class<?> implClass : concreteImplementations) {
                getLog().info("  - " + implClass.getName());
                if (verbose) {
                    getLog().debug("    位置: " + findClassLocation(implClass, urls));
                }
            }

        } catch (Exception e) {
            throw new MojoExecutionException("执行插件时发生错误", e);
        }
    }

    private boolean isAbstract(Class<?> cls) {
        return (cls.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0;
    }

    private String findClassLocation(Class<?> cls, List<URL> classpathUrls) {
        String className = cls.getName().replace('.', '/') + ".class";
        for (URL url : classpathUrls) {
            try {
                URL resourceUrl = new URL(url, className);
                try {
                    resourceUrl.openStream().close();
                    return url.toString();
                } catch (Exception ignored) {
                    // 不是这个URL
                }
            } catch (MalformedURLException ignored) {
                // 无效URL
            }
        }
        return "未知位置";
    }
}