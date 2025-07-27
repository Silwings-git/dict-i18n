package cn.silwings.dicti18n.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this class contains dictionary internationalization fields, used to optimize the framework's response enhancement performance.
 *
 * <p>When the framework processes a response object, it recursively scans the object structure to look for fields annotated with {@link DictDesc}, and fills in the internationalized text. </p>
 * <p>To avoid unnecessary deep recursion and field scanning, the framework checks at the type judgment stage whether the class is annotated with {@code @DictModel}, </p>
 * <p>so that it can quickly decide whether the class may contain fields that need processing, thereby improving overall processing performance. </p>
 * <p> Note: Annotating with {@code @DictModel} is not a prerequisite for recursion. The framework will always perform recursive scanning,
 * The main reason for this is that in many scenarios, it's not possible to add annotations for all returned types, such as when using third-party types that cannot be modified.</p>
 * <p>but adding this annotation helps the framework hit the cache early, avoiding deep analysis of unnecessary class structures. </p>
 * <p><strong>Example:</strong></p>
 * <pre>{@code
 * @DictModel
 * public class Address {
 * private String regionType;
 *
 * @DictDesc(RegionDict.class)
 * private String regionTypeDesc;
 * }
 *
 * public class UserInfo {
 * private String name;
 * // The 'address' will be recursively processed, and performance is better because 'Address' is annotated with @DictModel
 * private Address address;
 * }
 * }</pre>
 * <p>Recommended: For any class that contains other possible classes with dictionary fields, it is recommended to annotate the nested type with {@code @DictModel}. </p>
 *
 * @see DictDesc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictModel {
}
