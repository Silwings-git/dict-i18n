package cn.silwings.dicti18n.plugin.generate;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.plugin.scan.DictScanner;
import cn.silwings.dicti18n.plugin.scan.ScanContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;
import java.util.Set;

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

        // 确保目录存在
        if (!outputDir.exists()) {
            if (this.outputDir.mkdirs()) {
                this.getLog().info("An output directory has been created: " + this.outputDir.getAbsolutePath());
            } else {
                throw new MojoExecutionException("Unable to create output directory: " + this.outputDir.getAbsolutePath());
            }
        }
    }

    abstract void generate(final Set<Class<? extends Dict>> dictClassSet, final List<String> languages, final File outputDir);
}