package cn.silwings.dicti18n.loader.sql.init.mjdbc;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.sql.db.JdbcSQLTemplate;
import cn.silwings.dicti18n.loader.sql.db.SQLTemplate;
import lombok.Getter;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
public class MockJdbcTemplate implements SQLTemplate {

    private final List<String> schemaSqls = new CopyOnWriteArrayList<>();
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public void execute(final String sql) throws DataAccessException {
        this.schemaSqls.add(sql);
    }

    @Override
    public String queryForObject(final String sql, final Class<String> requiredType, final List<?> args) {
        if (sql.equals("SELECT description FROM dict_i18n WHERE dict_key = ? AND lang = ? AND enabled = 1 LIMIT 1")) {
            return this.cache.get(args.get(1) + "." + args.get(0));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> query(final String selectSql, final JdbcSQLTemplate.ResultMapper<T> rowMapper, final List<?> args) {
        if (selectSql.startsWith("SELECT dict_key FROM dict_i18n WHERE lang = ? AND dict_key IN (")) {
            final String language = (String) args.get(0);
            return args.stream().skip(1)
                    .map(arg -> language + "." + arg)
                    .map(this.cache::get)
                    .map(s -> (T) s.substring(s.lastIndexOf(".") + 1))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void update(final String updateSql, final List<?> args) {
        if (updateSql.startsWith("DELETE FROM dict_i18n WHERE lang = ? AND dict_key IN (")) {
            final String language = (String) args.get(0);
            args.stream().skip(1)
                    .map(arg -> language + "." + arg)
                    .forEach(this.cache::remove);
        }
    }

    @Override
    public <T> void batchUpdate(final String updateSql, final List<T> dataList, final BatchPreparedStatementSetter<T> batchPreparedStatementSetter) {
        if (updateSql.startsWith("INSERT INTO dict_i18n (dict_key, lang, description, enabled)")) {
            final String language;
            if (dataList.stream().anyMatch(data -> ((DictInfo) data).getDictDesc().equals("Pending"))) {
                language = "en-us";
            } else {
                language = "zh-cn";
            }
            dataList.stream()
                    .map(e -> (DictInfo) e)
                    .forEach(e -> this.cache.put(language + "." + e.getDictKey(), e.getDictDesc()));
        }
    }

    @Override
    public String getDatabaseProductName() {
        return "MySQL";
    }
}
