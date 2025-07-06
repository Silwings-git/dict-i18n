package cn.silwings.dicti18n.dict;

/**
 * Represents a dictionary item used for internationalized value descriptions.
 * <p>
 * Implementations of this interface define a structured way to associate a code (typically stored in the database)
 * with a dictionary namespace (called {@code dictName}) to facilitate localized text retrieval.
 * </p>
 *
 * <p>
 * This interface is typically implemented by enums to provide type-safe definitions of fixed dictionary sets,
 * such as order status, payment types, or gender. For example:
 * </p>
 *
 * <pre>{@code
 * public enum OrderStatus implements Dict {
 *     PENDING("pending"),
 *     SHIPPED("shipped");
 *
 *     private final String code;
 *
 *     OrderStatus(String dictName, String code) {
 *         this.dictName = dictName;
 *         this.code = code;
 *     }
 *
 *     @Override
 *     public String dictName() {
 *         return "orderStatus";
 *     }
 *
 *     @Override
 *     public String code() {
 *         return this.code;
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Implementations are used by {@code @DictDesc} to resolve localized display values
 * during API response enhancement or Excel export.
 * </p>
 */
public interface Dict {

    /**
     * Returns the dictionary namespace or key used for grouping related dictionary items.
     * <p>
     * Typically formatted like "order_status", this value is used as part of
     * the lookup key when resolving localized text (e.g. order_status.PENDING).
     * </p>
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