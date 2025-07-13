package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.plugin.scan.DictScanner;
import cn.silwings.dicti18n.plugin.scan.ScanContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDictGeneratorMojo extends AbstractMojo {

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

            final ScanContext context = new ScanContext(this.project, this.basePackages, this.verbose, this.getLog());
            final DictScanner scanner = new DictScanner();
            final Set<Class<? extends Dict>> dictClassSet = scanner.scan(context);

            this.initOutputDir();

            this.generate(dictClassSet, this.languages, this.outputDir);
        } catch (Exception e) {
            this.getLog().error("An error occurred while executing the plugin", e);
            if (e instanceof MojoExecutionException) {
                throw e;
            } else {
                throw new MojoExecutionException(e);
            }
        }
    }

    protected void initOutputDir() throws MojoExecutionException {
        if (null == this.outputDir) {
            this.outputDir = new File(this.project.getBasedir(), "src/main/resources/dict-i18n");
            this.getLog().info("The output directory is not specified, the default directory is used: " + this.outputDir.getAbsolutePath());
        } else {
            this.getLog().info("Use the user-specified output directory: " + this.outputDir.getAbsolutePath());
        }

        // Make sure the directory exists
        if (!outputDir.exists()) {
            if (this.outputDir.mkdirs()) {
                this.getLog().info("An output directory has been created: " + this.outputDir.getAbsolutePath());
            } else {
                throw new MojoExecutionException("Unable to create output directory: " + this.outputDir.getAbsolutePath());
            }
        }
    }

    void generate(final Set<Class<? extends Dict>> dictClassSet, final List<String> languages, final File outputDir) throws MojoExecutionException {
        final List<Dict[]> dictsList = new ArrayList<>();
        for (final Class<? extends Dict> dictClass : dictClassSet) {
            try {
                final Dict[] dictArray = dictClass.isEnum()
                        ? dictClass.getEnumConstants()
                        : new Dict[]{dictClass.getDeclaredConstructor().newInstance()};
                dictsList.add(dictArray);
            } catch (Exception e) {
                throw new MojoExecutionException(e);
            }
        }
        this.generate(dictsList, languages, outputDir);
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

    abstract void generate(final List<Dict[]> dictList, final List<String> languages, final File outputDir) throws MojoExecutionException;
}