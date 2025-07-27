package cn.silwings.dicti18n.loader.sql.db;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcSQLTemplate implements SQLTemplate {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSQLTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String queryForObject(final String sql, final Class<String> requiredType, final List<?> args) {
        return this.jdbcTemplate.queryForObject(sql, requiredType, args.toArray());
    }

    @Override
    public <T> List<T> query(final String selectSql, final ResultMapper<T> rowMapper, final List<?> args) {
        return this.jdbcTemplate.query(selectSql, (res, index) -> rowMapper.mapRow(res), args.toArray());
    }

    public interface ResultMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    @Override
    public void update(final String updateSql, final List<?> args) {
        this.jdbcTemplate.update(updateSql, args.toArray());
    }

    @Override
    public <T> void batchUpdate(final String updateSql, final List<T> dataList, final BatchPreparedStatementSetter<T> batchPreparedStatementSetter) {
        this.jdbcTemplate.batchUpdate(updateSql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                batchPreparedStatementSetter.setValues(ps, i, dataList.get(i));
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });
    }

    @Override
    public void execute(final String sql) {
        this.jdbcTemplate.execute(sql);
    }

    @Override
    public String getDatabaseProductName() {
        try {
            final DataSource dataSource = this.jdbcTemplate.getDataSource();
            if (null == dataSource) {
                throw new IllegalStateException("[DictI18n] JdbcTemplate has no DataSource");
            }
            final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            return metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException("[DictI18n] Failed to get database product name", e);
        }
    }
}
