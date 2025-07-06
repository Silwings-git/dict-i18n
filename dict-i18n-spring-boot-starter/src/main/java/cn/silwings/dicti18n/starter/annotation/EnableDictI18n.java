package cn.silwings.dicti18n.starter.annotation;

import cn.silwings.dicti18n.starter.advice.DictI18nResponseEnhancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to explicitly enable dictionary internationalization
 * enhancement for a specific controller class or method.
 *
 * <p>
 * When applied, {@link DictI18nResponseEnhancer} will post-process the response
 * body for internationalization, injecting localized dictionary descriptions
 * according to the current language.
 * </p>
 *
 * <p>
 * Can be placed on a controller class or an individual method.
 * Method-level annotations take precedence over class-level ones.
 * </p>
 *
 * <pre>{@code
 * @EnableDictI18n
 * @RestController
 * public class I18nController {
 *     @GetMapping("/localized")
 *     public SomeResponse get() { ... }
 * }
 * }</pre>
 *
 * @see DisableDictI18n
 * @see DictI18nResponseEnhancer
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDictI18n {
}


