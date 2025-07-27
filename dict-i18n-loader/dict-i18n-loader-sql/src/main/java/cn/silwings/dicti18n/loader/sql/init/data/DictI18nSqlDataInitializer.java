package cn.silwings.dicti18n.loader.sql.init.data;

import cn.silwings.dicti18n.loader.enums.PreLoadMode;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dictionary internationalization data initializer, used to batch import dictionary data into the database
 */
public class DictI18nSqlDataInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DictI18nSqlDataInitializer(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Initialize multilingual dictionary data
     *
     * @param langDictMap Mapping of language codes to dictionary information lists
     * @param preloadMode Preload mode
     */
    @Transactional(rollbackFor = Exception.class)
    public void initialize(final Map<String, List<DictInfo>> langDictMap, final PreLoadMode preloadMode) {
        langDictMap.forEach((language, dictList) -> this.initializeByLanguage(language, dictList, preloadMode));
    }

    /**
     * Initialize dictionary data by language
     *
     * @param language    language code
     * @param dictList    Dictionary information list
     * @param preloadMode Preload mode
     */
    private void initializeByLanguage(final String language, final List<DictInfo> dictList, final PreLoadMode preloadMode) {
        if (null == language || null == dictList || dictList.isEmpty()) {
            return;
        }

        switch (preloadMode) {
            case FULL:
                this.executeFullUpdate(language, dictList);
                break;
            case INCREMENTAL:
                this.executeIncrementalUpdate(language, dictList);
                break;
            default:
                throw new IllegalArgumentException("[DictI18n] Unsupported preload mode: " + preloadMode);
        }
    }

    /**
     * Perform incremental updates - only insert data that does not exist in the database
     *
     * @param language language code
     * @param dictList Dictionary information list
     */
    private void executeIncrementalUpdate(final String language, final List<DictInfo> dictList) {

        final List<String> params = dictList.stream().map(DictInfo::getDictKey).distinct().collect(Collectors.toList());

        // Query existing data
        final String selectSql = "SELECT dict_key FROM dict_i18n WHERE lang = ? AND dict_key IN (" +
                                 String.join(",", Collections.nCopies(params.size(), "?")) + ")";

        // The first parameter of SQL is the language code.
        params.add(0, language);

        final List<String> dictKeyList = this.jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("dict_key"), params.toArray());
        final Set<String> existingKeys = new HashSet<>(dictKeyList);

        // Filter out the new data that needs to be inserted.
        final List<DictInfo> newItems = dictList.stream()
                .filter(dictInfo -> !existingKeys.contains(dictInfo.getDictKey()))
                .collect(Collectors.toList());

        if (!newItems.isEmpty()) {
            this.batchInsert(language, newItems);
        }
    }

    /**
     * Perform a full update - first delete existing data, then insert all data
     *
     * @param language language code
     * @param dictList Dictionary information list
     */
    private void executeFullUpdate(final String language, final List<DictInfo> dictList) {

        final List<String> params = dictList.stream().map(DictInfo::getDictKey).distinct().collect(Collectors.toList());

        // Delete all dict_key records that match the language and exist in DictList
        if (!params.isEmpty()) {
            final String deleteSql = "DELETE FROM dict_i18n WHERE lang = ? AND dict_key IN (" +
                                     String.join(",", Collections.nCopies(params.size(), "?")) + ")";
            // The first parameter of SQL is the language code.
            params.add(0, language);
            this.jdbcTemplate.update(deleteSql, params.toArray());
        }

        this.batchInsert(language, dictList);
    }

    /**
     * Batch insert dictionary data
     *
     * @param language language code
     * @param dictList Dictionary information list
     */
    private void batchInsert(final String language, final List<DictInfo> dictList) {
        final String insertSql = "INSERT INTO dict_i18n (dict_key, lang, description, enabled) " +
                                 "VALUES (?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                final DictInfo item = dictList.get(i);
                ps.setString(1, item.getDictKey());
                ps.setString(2, language);
                ps.setString(3, item.getDictDesc());
                ps.setInt(4, 1);
            }

            @Override
            public int getBatchSize() {
                return dictList.size();
            }
        });
    }

}