package cn.silwings.dicti18n.declared.dict;

import cn.silwings.dicti18n.dict.Dict;

/**
 * A Dict implementation that provides a declared (language-independent) description.
 * This is typically used in `DeclaredDictLoader` for direct access.
 */
public interface DeclaredDict extends Dict {

    /**
     * Returns the declared description, not affected by language.
     */
    String getDesc();

}