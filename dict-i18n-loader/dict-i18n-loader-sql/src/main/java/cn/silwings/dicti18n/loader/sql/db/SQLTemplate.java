package cn.silwings.dicti18n.loader.sql.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface SQLTemplate {
    String queryForObject(String sql, Class<String> requiredType, List<?> args);

    <T> List<T> query(String selectSql, JdbcSQLTemplate.ResultMapper<T> rowMapper, List<?> args);

    void update(String updateSql, List<?> args);

    <T> void batchUpdate(String updateSql, List<T> dataList, BatchPreparedStatementSetter<T> batchPreparedStatementSetter);

    void execute(String sql);

    String getDatabaseProductName();

    @FunctionalInterface
    interface BatchPreparedStatementSetter<T> {

        /**
         * Set parameter values on the given PreparedStatement.
         *
         * @param ps    the PreparedStatement to invoke setter methods on
         * @param index index of the statement we're issuing in the batch, starting from 0
         * @param data  Index corresponding data
         * @throws SQLException if an SQLException is encountered
         *                      (i.e. there is no need to catch SQLException)
         */
        void setValues(PreparedStatement ps, int index, T data) throws SQLException;

    }

}
