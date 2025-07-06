package cn.silwings.dicti18n.annotation;

import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.processor.DictI18nProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field as a "dictionary description field", used for automatically filling in the corresponding internationalized text based on the dictionary value of another field (usually the code of an enum).
 *
 * <p>This annotation is typically used in Web return objects, for example:</p>
 *
 * <pre>{@code
 * public class OrderInfo {
 *     private String orderStatus;
 *
 *     @DictDesc(OrderStatus.class)
 *     private String orderStatusDesc;
 *
 *     @DictDesc(value = OrderStatus.class, field = "orderStatus")
 *     private String remark;
 * }
 * }</pre>
 *
 * <p>The framework will automatically fill in the text based on the {@link #value()} specified enum class, finding the matching enum item and assigning its {@code getText()} result to the field, provided that the enum class implements the {@link Dict} interface.</p>
 * <p>
 * {@link DictI18nProcessor} supports directly processing the annotated object.
 *
 * <p>For example, when orderStatus is "waitPay", the framework will look for the enum item in the OrderStatus enum class with {@code code()} equal to "waitPay" and assign its {@code getText()} result to the orderStatusDesc field.</p>
 *
 * <p>If the {@link #field()} is not specified, it will default to inferring the corresponding dictionary value field name by removing the "Desc" suffix from the current field name, for example, "orderStatusDesc" corresponds to "orderStatus" field.</p>
 *
 * <p>If the dictionary value field name cannot be inferred by the default rule or the field name is different, the {@link #field()} can be explicitly specified to indicate the dictionary value field name.</p>
 *
 * <p>This annotation is typically used in conjunction with the {@link Dict} enum interface for uniformly managing the binding between dictionary values and display texts.</p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictDesc {

    /**
     * Specifies an enumeration class to be used to translate dictionary values, which must implement the {@link Dict} interface.
     */
    Class<? extends Dict> value();

    /**
     * Specify the dictionary code field name, which is the name obtained by removing the "Desc" suffix from the field name of the current field.
     *
     * <p>For example, if the field name is "orderStatusDesc", it corresponds to "orderStatus" by default.
     * If the actual corresponding field name is different, you need to specify this attribute explicitly, otherwise the corresponding translation will not be found</p>
     */
    String field() default "";
}
