package cn.silwings.dicti18n.plugin.scan;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scans the classpath for implementation classes of the {@link Dict} interface.
 * <p>
 * This scanner uses Reflections library to search for non-abstract, non-interface classes
 * that implement the Dict interface, optionally limited by base packages.
 * </p>
 */
public class DictScanner {

    /**
     * Scans for all concrete (non-abstract, non-interface) classes that implement the {@link Dict} interface.
     *
     * @param context the context containing configuration and Maven project/classpath info
     * @return a set of classes implementing the Dict interface
     * @throws MojoExecutionException if scanning fails
     */
    @SuppressWarnings("unchecked")
    public Set<Class<? extends Dict>> scan(final ScanContext context) throws MojoExecutionException {

        final Log log = context.getLog();
        log.info("Start looking for the implementation class of the Dict interface...");

        try {
            final MavenProject currentProject = context.getProject();
            final ClassLoader classLoader = context.getClassLoader();
            final File file = new File(currentProject.getBuild().getOutputDirectory());

            final Set<Class<? extends Dict>> result = scanClassNames(file)
                    .stream()
                    .map(className -> {
                        try {
                            return Class.forName(className, true, classLoader);
                        } catch (ClassNotFoundException e) {
                            log.error(e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(cls -> !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers()))
                    .filter(Dict.class::isAssignableFrom)
                    .map(cls -> (Class<? extends Dict>) cls)
                    .collect(Collectors.toSet());

            log.info("[DictScanner] Found " + result.size() + " concrete Dict classes.");
            for (Class<?> implClass : result) {
                log.info("[DictScanner] -> " + implClass.getName());
                if (context.isVerbose()) {
                    logMore(context, implClass);
                }
            }

            return result;
        } catch (Exception e) {
            throw new MojoExecutionException("[DictI18n] Dict implementation of class-like scan failed", e);
        }
    }

    private static void logMore(final ScanContext context, final Class<?> implClass) {
        try {
            context.getLog().debug("    classloader: " + implClass.getClassLoader());
            context.getLog().debug("    modifiers: " + Modifier.toString(implClass.getModifiers()));
            context.getLog().debug("    extends: " + (implClass.getSuperclass() != null ? implClass.getSuperclass().getName() : "none"));
            context.getLog().debug("    interfaces: " + Arrays.stream(implClass.getInterfaces())
                    .map(Class::getName).collect(Collectors.joining(", ")));
            context.getLog().debug("    annotations: " +
                    Arrays.stream(implClass.getAnnotations())
                            .map(a -> "@" + a.annotationType().getSimpleName())
                            .collect(Collectors.joining(", ")));
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Scan the directory and extract all class names
     *
     * @param directory The directory to scan
     * @return Full list of class names (including package names)
     */
    public static List<String> scanClassNames(final File directory) {
        final List<String> classNames = new ArrayList<>();
        scanForClassNames(directory, "", classNames);
        return classNames;
    }

    private static void scanForClassNames(final File dir, final String packageName, final List<String> classNames) {
        final File[] files = dir.listFiles();
        if (null == files) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                final String newPackage = packageName.isEmpty()
                        ? file.getName()
                        : packageName + "." + file.getName();
                scanForClassNames(file, newPackage, classNames);
            } else if (file.getName().endsWith(".class")) {
                // Extract class name (remove the .class suffix)
                final String className = file.getName().substring(0, file.getName().length() - 6);
                // Combination full class name (package name + class name)
                final String fullClassName = packageName.isEmpty()
                        ? className
                        : packageName + "." + className;
                classNames.add(fullClassName);
            }
        }
    }
}