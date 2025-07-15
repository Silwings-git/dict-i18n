package cn.silwings.dicti18n.starter.enhancer.filter;

import org.springframework.core.MethodParameter;

/**
 * Default implementation of {@link DictI18nResponseFilter} that always returns {@code true}.
 * <p>
 * This means that dictionary internationalization enhancement will be applied
 * to all controller responses without any filtering.
 * </p>
 *
 * <p>
 * This implementation is typically used as the fallback when the user does not provide
 * a custom filter.
 * </p>
 */
public class AlwaysTrueDictI18nResponseFilter implements DictI18nResponseFilter {
    /**
     * Always return {@code true}, indicating that response enhancement should be applied.
     *
     * @param returnType    the return type of the controller method
     * @param converterType the type of message converter being used
     * @return {@code true} always
     */
    @Override
    public boolean shouldEnhance(final MethodParameter returnType, final Class<?> converterType) {
        return true;
    }
}
