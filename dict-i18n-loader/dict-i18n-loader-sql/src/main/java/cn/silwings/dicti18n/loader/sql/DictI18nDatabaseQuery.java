package cn.silwings.dicti18n.loader.sql;

import java.util.Optional;

@FunctionalInterface
public interface DictI18nDatabaseQuery {

    Optional<String> select(String lang, String dictKey);

}