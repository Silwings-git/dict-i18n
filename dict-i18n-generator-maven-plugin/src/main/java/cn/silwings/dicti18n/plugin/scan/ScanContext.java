package cn.silwings.dicti18n.plugin.scan;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class ScanContext {

    private final MavenProject project;

    private final List<String> basePackages;

    private final boolean verbose;

    private final Log log;

    public ScanContext(final MavenProject project, final List<String> basePackages, final boolean verbose, final Log log) {
        this.project = project;
        this.basePackages = basePackages;
        this.verbose = verbose;
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public MavenProject getProject() {
        return project;
    }

    public List<String> getBasePackages() {
        return basePackages;
    }

    public boolean isVerbose() {
        return verbose;
    }
}