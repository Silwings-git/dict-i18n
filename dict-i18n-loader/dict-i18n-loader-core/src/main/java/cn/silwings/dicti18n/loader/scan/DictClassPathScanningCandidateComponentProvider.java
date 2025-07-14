package cn.silwings.dicti18n.loader.scan;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Custom implementation of Spring's ClassPathScanningCandidateComponentProvider
 * for scanning dictionary-related classes.
 * <p>
 * It overrides the candidate component check to include only classes that are
 * independent (top-level or static nested classes) and not interfaces.
 */
public class DictClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

    /**
     * Creates a new instance with the specified default filter usage.
     *
     * @param useDefaultFilters whether to register the default filters for the @Component, @Repository, @Service, and @Controller stereotype annotations
     */
    public DictClassPathScanningCandidateComponentProvider(final boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    /**
     * Determine whether the given bean definition qualifies as a candidate component.
     * This implementation restricts candidates to classes that are independent and not interfaces,
     * filtering out inner classes and interfaces.
     *
     * @param beanDefinition the bean definition to check
     * @return true if the bean definition is an independent class and not an interface
     */
    @Override
    protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
        final AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && !metadata.isInterface();
    }
}