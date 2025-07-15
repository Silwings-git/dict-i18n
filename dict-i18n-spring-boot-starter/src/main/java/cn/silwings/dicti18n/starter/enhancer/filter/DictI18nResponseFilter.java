package cn.silwings.dicti18n.starter.enhancer.filter;

import cn.silwings.dicti18n.starter.enhancer.DictI18nResponseEnhancer;
import org.springframework.core.MethodParameter;

/**
 * Filter interface to determine whether the response body should be enhanced
 * with dictionary internationalization by {@link DictI18nResponseEnhancer}.
 * <p>
 * Users can implement this interface to define custom conditions for applying
 * dictionary i18n injection logic on controller responses.
 * </p>
 * <p>
 * If no implementation is provided, a default implementation
 * ({@link AlwaysTrueDictI18nResponseFilter}) will be used,
 * which enables enhancement for all responses.
 * </p>
 */
@FunctionalInterface
public interface DictI18nResponseFilter {
    boolean shouldEnhance(MethodParameter returnType, Class<?> converterType);
}