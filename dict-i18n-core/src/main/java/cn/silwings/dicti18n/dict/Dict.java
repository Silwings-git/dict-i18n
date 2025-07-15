package cn.silwings.dicti18n.dict;

/**
 * Represents a dictionary item used for internationalized value descriptions.
 *
 * <p>
 * Implementations of this interface define a structured way to associate a code (typically stored in the database)
 * with a dictionary namespace (called {@code dictName}) to facilitate localized text retrieval.
 * </p>
 *
 * <h3>Implementation Types</h3>
 * This interface can be implemented in two ways:
 * <ul>
 *   <li><strong>Enum-based</strong>: Recommended for static and predefined dictionaries
 *       (e.g. order status, gender). These are type-safe, support scanning, configuration export,
 *       and web API introspection.</li>
 *   <li><strong>JavaBean-based</strong>: Suitable for dynamic dictionaries where items are not known at compile time.
 *       These classes must have a no-arg constructor and can return a dummy value (e.g. {@code null} or {@code "*"}) from {@link #code()}.
 *       Their primary purpose is to define the {@code dictName()}, which acts as a configuration anchor.</li>
 * </ul>
 *
 * <p>
 * Tools such as the <code>dict-i18n-generator-maven-plugin</code> will include both Enum and JavaBean-based implementations
 * when generating configuration files (e.g. YML, SQL), ensuring users can conveniently manage all dictionary sources.
 * </p>
 *
 * <p><strong>Note:</strong> Runtime features that enumerate all dictionary items (e.g. web API <code>/dict/items</code>)
 * only support Enum-based dictionaries, as JavaBean implementations are designed to work with dynamic data sources
 * (e.g. MySQL, Redis) where dictionary codes are determined externally at runtime.
 * </p>
 *
 * <h3>Example (Enum):</h3>
 * <pre>{@code
 * public enum OrderStatus implements Dict {
 *     PENDING("pending"),
 *     SHIPPED("shipped");
 *
 *     private final String code;
 *
 *     OrderStatus(String code) {
 *         this.code = code;
 *     }
 *
 *     @Override
 *     public String dictName() {
 *         return "order_status";
 *     }
 *
 *     @Override
 *     public String code() {
 *         return this.code;
 *     }
 * }
 * }</pre>
 * <p>
 * This interface is commonly used in conjunction with {@code @DictDesc} to support response enhancement and configuration generation across various internationalization loaders.
 * </p>
 */
public interface Dict {

    /**
     * Returns the dictionary name (namespace) used for grouping related dictionary items.
     * <p>
     * Typically formatted like {@code "order_status"}, this value is used as part of
     * the lookup key when resolving localized text (e.g. {@code order_status.PENDING}).
     * </p>
     *
     * <p><strong>Important:</strong> If {@code Dict} is implemented by an {@code enum},
     * all enum constants in the same enum class must return the same {@code dictName}.
     * Returning different values will result in undefined behavior in dictionary scanning,
     * caching, or configuration generation.</p>
     *
     * @return the dictionary name (namespace)
     */
    String dictName();

    /**
     * Returns the internal code or identifier of a dictionary item.
     * <p>
     * This value usually comes from the business object (e.g. database field),
     * and is used to match against the localized dictionary value.
     * </p>
     *
     * @return the code representing a dictionary item
     */
    String code();

}