package cn.silwings.dicti18n.starter.annotation;

import cn.silwings.dicti18n.starter.enhancer.DictI18nResponseEnhancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to explicitly disable dictionary internationalization
 * enhancement for a specific controller class or method.
 *
 * <p>
 * When applied, {@link DictI18nResponseEnhancer} will skip post-processing
 * the response body for internationalization, regardless of global configuration.
 * </p>
 *
 * <p>
 * Can be placed on a controller class or an individual method.
 * Method-level annotations take precedence over class-level ones.
 * </p>
 *
 * <pre>{@code
 * @DisableDictI18n
 * @RestController
 * public class NoI18nController {
 *     // This method will not be enhanced
 *     @GetMapping("/example")
 *     public SomeResponse get() { ... }
 * }
 * }</pre>
 *
 * @see EnableDictI18n
 * @see DictI18nResponseEnhancer
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableDictI18n {
}