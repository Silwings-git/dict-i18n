package cn.silwings.dicti18n.loader.cache;

import java.util.Optional;

@FunctionalInterface
public interface DictDescGetter {

    Optional<String> get(String lang, String dictKey);

}