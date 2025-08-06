package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.plugin.scan.DictScanner;
import cn.silwings.dicti18n.plugin.scan.ScanContext;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractDictGeneratorMojo extends AbstractMojo {
    // Match class names similar to "com.example.EnableStatusEnum$1"
    private static final Pattern ENUM_ANONYMOUS_CLASS_PATTERN = Pattern.compile(".*\\$\\d+$");

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    protected MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "basePackages", required = true)
    protected List<String> basePackages;

    @Parameter(property = "languages", required = true)
    protected List<String> languages;

    @Parameter(property = "outputDir")
    protected File outputDir;

    @Parameter(property = "verbose", defaultValue = "false")
    protected boolean verbose;

    @Override
    public void execute() throws MojoExecutionException {
        try {

            if (null == this.basePackages) {
                this.basePackages = new ArrayList<>();
            }
            if (this.basePackages.isEmpty()) {
                String sourceDir = this.project.getBuild().getSourceDirectory();
                final File sourceDirectory = new File(sourceDir);
                if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
                    getLog().warn("The source code directory does not exist: " + sourceDir);
                    return;
                }
                List<String> packages = this.findPackagesInDirectory(sourceDirectory, sourceDirectory);
                this.getLog().info("The base package is not specified, the default package is used: " + packages.stream().collect(Collectors.joining(",")));
                this.basePackages.addAll(packages);
            }

            try (final URLClassLoader classLoader = this.buildClassLoader()) {
                final ScanContext context = new ScanContext(this.project, this.basePackages, classLoader, this.verbose, this.getLog());
                final DictScanner scanner = new DictScanner();
                final Set<Class<? extends Dict>> dictClassSet = scanner.scan(context);

                if (!dictClassSet.isEmpty()) {
                    this.initOutputDir();
                    this.generate(dictClassSet, this.languages, this.outputDir, classLoader);
                }
            }
        } catch (Exception e) {
            this.getLog().error("An error occurred while executing the plugin", e);
            if (e instanceof MojoExecutionException) {
                throw (MojoExecutionException) e;
            } else {
                throw new MojoExecutionException(e);
            }
        }
    }

    private URLClassLoader buildClassLoader() throws DependencyResolutionRequiredException {
        final Set<URI> uris = new HashSet<>();

        for (final MavenProject mavenProject : this.session.getProjects()) {
            for (String element : mavenProject.getCompileClasspathElements()) {
                uris.add(new File(element).toURI());
            }
        }

        return new URLClassLoader(uris.stream().map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        this.getLog().error("[Dict-I18n] An error occurred while trying to build the classloader for " + uri, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull).toArray(URL[]::new), this.getClass().getClassLoader());
    }

    protected void initOutputDir() throws MojoExecutionException {
        if (null == this.outputDir) {
            this.outputDir = new File(this.project.getBasedir(), "src/main/resources/dict_i18n");
            this.getLog().info("The output directory is not specified, the default directory is used: " + this.outputDir.getAbsolutePath());
        } else {
            this.getLog().info("Use the user-specified output directory: " + this.outputDir.getAbsolutePath());
        }

        // Make sure the directory exists
        if (!this.outputDir.exists()) {
            if (this.outputDir.mkdirs()) {
                this.getLog().info("An output directory has been created: " + this.outputDir.getAbsolutePath());
            } else {
                throw new MojoExecutionException("[DictI18n] Unable to create output directory: " + this.outputDir.getAbsolutePath());
            }
        }
    }

    void generate(final Set<Class<? extends Dict>> dictClassSet, final List<String> languages, final File outputDir, final ClassLoader classLoader) throws MojoExecutionException {
        final List<Dict[]> dictsList = new ArrayList<>();
        final Set<Class<?>> visited = new HashSet<>();
        for (final Class<? extends Dict> dictClass : dictClassSet) {
            try {
                final Dict[] dictInstances = this.getDictInstances(dictClass, classLoader, visited);
                if (null != dictInstances && dictInstances.length > 0) {
                    dictsList.add(dictInstances);
                }
            } catch (Exception e) {
                throw new MojoExecutionException(e);
            }
        }
        if (!dictsList.isEmpty()) {
            this.generate(dictsList, languages, outputDir);
        }
    }

    private List<String> findPackagesInDirectory(final File rootDir, final File currentDir) {
        final List<String> packages = new ArrayList<>();

        final File[] files = currentDir.listFiles();
        if (null == files) {
            return packages;
        }

        boolean hasJavaFiles = false;
        for (File file : files) {
            if (file.isDirectory()) {
                packages.addAll(this.findPackagesInDirectory(rootDir, file));
            } else if (file.getName().endsWith(".java")) {
                hasJavaFiles = true;
            }
        }

        // If the current directory contains Java files, calculate the package name
        if (hasJavaFiles) {
            final String packageName = this.calculatePackageName(rootDir, currentDir);
            if (!packageName.isEmpty()) {
                packages.add(packageName);
            }
        }

        return packages;
    }

    private String calculatePackageName(final File rootDir, final File dir) {
        final String rootPath = rootDir.getAbsolutePath();
        final String dirPath = dir.getAbsolutePath();

        if (!dirPath.startsWith(rootPath)) {
            return "";
        }

        // Calculate relative paths
        String relPath = dirPath.substring(rootPath.length()).replace(File.separatorChar, '.');
        if (relPath.startsWith(".")) {
            relPath = relPath.substring(1);
        }

        return relPath;
    }

    protected Dict[] getDictInstances(final Class<? extends Dict> dictClass, final ClassLoader classLoader, final Set<Class<?>> visited) throws Exception {

        try {
            if (!visited.add(dictClass)) {
                return null;
            }

            if (dictClass.isEnum()) {
                return dictClass.getEnumConstants();
            }

            // Check if it is an anonymous subclass of an enum
            if (isAnonymousEnumSubclass(dictClass)) {
                final Class<?> outerEnumClass = this.getOuterEnumClass(dictClass, classLoader);
                if (!visited.add(outerEnumClass)) {
                    return null;
                }
                if (null != outerEnumClass && outerEnumClass.isEnum()) {
                    return (Dict[]) outerEnumClass.getEnumConstants();
                }
            }

            return new Dict[]{dictClass.getDeclaredConstructor().newInstance()};
        } catch (Exception e) {
            this.getLog().error("Failed to get Dict instance: " + dictClass.getName(), e);
            return null;
        }
    }

    private Class<?> getOuterEnumClass(final Class<?> clazz, final ClassLoader classLoader) throws ClassNotFoundException {
        final String className = clazz.getName();
        final int dollarIndex = className.lastIndexOf('$');
        if (dollarIndex == -1) {
            // Not an inner class
            return null;
        }
        final String outerClassName = className.substring(0, dollarIndex);
        return Class.forName(outerClassName, true, classLoader);
    }

    /**
     * Check if the given class is an anonymous subclass of an enum.
     *
     * @param clazz Class to check
     * @return Return true if it is an anonymous subclass of an enumeration, otherwise return false.
     */
    public static boolean isAnonymousEnumSubclass(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }

        if (!Enum.class.isAssignableFrom(clazz)) {
            return false;
        }

        // Check if it is an anonymous class (the class name contains $ followed by numbers)
        final String className = clazz.getName();
        final int lastDollarIndex = className.lastIndexOf('$');
        if (lastDollarIndex == -1) {
            return false;
        }

        final String suffix = className.substring(lastDollarIndex + 1);
        if (suffix.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(suffix);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    abstract void generate(final List<Dict[]> dictList, final List<String> languages, final File outputDir) throws MojoExecutionException;
}