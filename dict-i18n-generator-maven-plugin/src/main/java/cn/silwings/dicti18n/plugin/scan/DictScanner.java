package cn.silwings.dicti18n.plugin.scan;

import cn.silwings.dicti18n.dict.Dict;
import org.apache.maven.plugin.MojoExecutionException;
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
    public Set<Class<? extends Dict>> scan(final ScanContext context) throws MojoExecutionException {

        context.getLog().info("Start looking for the implementation class of the Dict interface...");

        try {
            // Get the compiled classpath
            final List<String> classpathElements = context.getProject().getCompileClasspathElements();
            final List<URL> urls = new ArrayList<>();

            for (String element : classpathElements) {
                try {
                    urls.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    context.getLog().error("Conversion of classpath element failed: " + element, e);
                }
            }

            // Create a classloader
            try (final URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader())) {
                // Obtain the Reflections configuration
                ConfigurationBuilder config = new ConfigurationBuilder()
                        .setClassLoaders(new ClassLoader[]{classLoader})
                        .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated);

                // Add a scan path
                if (context.getBasePackages() != null && !context.getBasePackages().isEmpty()) {
                    config.setUrls(urls.stream()
                            .flatMap(url -> context.getBasePackages()
                                    .stream()
                                    .map(pkg -> url.toString() + "/" + pkg.replace('.', '/')))
                            .map(path -> {
                                try {
                                    return new URL(path);
                                } catch (MalformedURLException e) {
                                    context.getLog().warn("Invalid base package path: " + path, e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
                } else {
                    config.setUrls(urls);
                }

                // Scanning classes
                final Reflections reflections = new Reflections(config);

                // Find all implementation classes
                final Set<Class<? extends Dict>> allImplementations = reflections.getSubTypesOf(Dict.class);

                // Filter out abstract classes and interfaces
                final Set<Class<? extends Dict>> concreteImplementations = allImplementations.stream()
                        .filter(cls -> !cls.isInterface() && !isAbstract(cls))
                        .collect(Collectors.toSet());

                // Output the result
                context.getLog().info("Find the implementation classes for the " + concreteImplementations.size() + " Dict interfaces ");
                for (Class<? extends Dict> implClass : concreteImplementations) {
                    context.getLog().info("  - " + implClass.getName());
                    if (context.isVerbose()) {
                        context.getLog().debug("    location: " + findClassLocation(implClass, urls));
                    }
                }

                return concreteImplementations;
            }
        } catch (Exception e) {
            throw new MojoExecutionException("[DictI18n] Dict implementation of class-like scan failed", e);
        }
    }

    /**
     * Checks whether a class is abstract.
     *
     * @param cls the class to check
     * @return true if abstract, false otherwise
     */
    public boolean isAbstract(final Class<?> cls) {
        return (cls.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0;
    }

    /**
     * Attempts to determine the URL location of a class file.
     *
     * @param cls           the class whose location is to be found
     * @param classpathUrls list of classpath root URLs to search in
     * @return the URL as a string, or "Unknown location" if not found
     */
    public String findClassLocation(final Class<?> cls, final List<URL> classpathUrls) {
        final String className = cls.getName().replace('.', '/') + ".class";
        for (URL url : classpathUrls) {
            try {
                final URL resourceUrl = new URL(url, className);
                try {
                    resourceUrl.openStream().close();
                    return url.toString();
                } catch (Exception ignored) {
                    // Not this URL
                }
            } catch (MalformedURLException ignored) {
                // Invalid URL
            }
        }
        return "Unknown location";
    }

}