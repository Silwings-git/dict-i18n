package cn.silwings.dicti18n.starter.enhancer;

import cn.silwings.dicti18n.processor.DictI18nProcessor;
import cn.silwings.dicti18n.starter.annotation.DisableDictI18n;
import cn.silwings.dicti18n.starter.annotation.EnableDictI18n;
import cn.silwings.dicti18n.starter.config.DictI18nStarterProperties;
import cn.silwings.dicti18n.starter.config.LanguageProvider;
import cn.silwings.dicti18n.starter.enhancer.filter.DictI18nResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Spring MVC {@link ResponseBodyAdvice} implementation that post-processes HTTP response bodies
 * to inject internationalized dictionary descriptions based on the current user's language.
 *
 * <p>This enhancer will automatically traverse the response body and enhance fields
 * marked with dictionary metadata (e.g., {@code @DictDesc}).</p>
 *
 * <p>The behavior can be controlled through multiple mechanisms:</p>
 * <ul>
 *     <li>{@link DictI18nStarterProperties} global configuration</li>
 *     <li>{@link EnableDictI18n} and {@link DisableDictI18n} annotations on controllers or methods</li>
 *     <li>Custom filtering logic via {@link DictI18nResponseFilter}</li>
 * </ul>
 *
 * <p>Enhancement is only applied if:
 * <ul>
 *     <li>Enhancer is globally enabled (via config)</li>
 *     <li>No exclusion annotations match the controller or method</li>
 *     <li>Package inclusion rules pass</li>
 *     <li>The custom filter returns true</li>
 * </ul>
 * </p>
 */
@ControllerAdvice
public class DictI18nResponseEnhancer implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(DictI18nResponseEnhancer.class);

    private final DictI18nProcessor processor;
    private final LanguageProvider languageProvider;
    private final DictI18nResponseFilter dictI18nResponseFilter;
    private final DictI18nStarterProperties properties;

    public DictI18nResponseEnhancer(final DictI18nProcessor processor,
                                    final LanguageProvider languageProvider,
                                    final DictI18nResponseFilter dictI18nResponseFilter,
                                    final DictI18nStarterProperties dictI18nStarterProperties) {
        this.processor = processor;
        this.languageProvider = languageProvider;
        this.dictI18nResponseFilter = dictI18nResponseFilter;
        this.properties = dictI18nStarterProperties;
    }

    /**
     * Determine whether the given response should be intercepted and enhanced.
     *
     * @param returnType    the return type of the controller method
     * @param converterType the selected converter type
     * @return {@code true} if the response should be enhanced
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        // Global switch from configuration
        if (!this.properties.getEnhancer().isEnabled()) {
            log.info("[DictI18n] Response enhancer is disabled.");
            return false;
        }

        final Method method = returnType.getMethod();
        final Class<?> controllerClass = returnType.getContainingClass();

        // Annotation Priority Judgment (Method > Class)
        if (null != method) {
            if (method.isAnnotationPresent(DisableDictI18n.class)) {
                return false;
            }
            if (method.isAnnotationPresent(EnableDictI18n.class)) {
                return true;
            }
        }
        if (controllerClass.isAnnotationPresent(DisableDictI18n.class)) {
            return false;
        }
        if (controllerClass.isAnnotationPresent(EnableDictI18n.class)) {
            return true;
        }

        // Filter by configured included packages (if set)
        if (!this.properties.getEnhancer().getIncludePackages().isEmpty()) {
            final String className = returnType.getParameterType().getName();
            final boolean matched = this.properties.getEnhancer().getIncludePackages().stream().anyMatch(className::startsWith);
            if (!matched) {
                return false;
            }
        }

        // Filter by configured exclusion annotations (by class name)
        for (String annotationClassName : this.properties.getEnhancer().getExcludeAnnotations()) {
            try {
                final Class<?> annotationClass = Class.forName(annotationClassName);
                if (controllerClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass) ||
                        (null != method && method.isAnnotationPresent((Class<? extends Annotation>) annotationClass))) {
                    return false;
                }
            } catch (ClassNotFoundException e) {
                log.warn("[DictI18n] Exclude annotation class not found: {}", annotationClassName, e);
            }
        }

        // Custom user-defined filter logic
        return this.dictI18nResponseFilter.shouldEnhance(returnType, converterType);
    }

    /**
     * Enhance the response body after controller method execution.
     *
     * @param body                  the body to be written to the response
     * @param returnType            the return type of the controller method
     * @param selectedContentType   the content type selected
     * @param selectedConverterType the converter type selected
     * @param request               the current request
     * @param response              the current response
     * @return the potentially enhanced body
     */
    @Override
    public Object beforeBodyWrite(final Object body,
                                  final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request,
                                  final ServerHttpResponse response) {
        this.processor.process(body, this.languageProvider.getCurrentLanguage());
        return body;
    }
}
