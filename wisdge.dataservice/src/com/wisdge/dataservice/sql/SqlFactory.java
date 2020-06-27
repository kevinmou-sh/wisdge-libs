package com.wisdge.dataservice.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.ResultSetMetaData;
import java.util.*;

public class SqlFactory {
    private static Logger logger = LoggerFactory.getLogger(SqlFactory.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private SqlTemplateManager sqlTemplateManager;

    public <T> T queryForObject(String sqlKey, Map<String, Object> whereExpress, Class<T> requiredType, Object...args) throws DataAccessException {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        long start = new Date().getTime();
        logger.debug("[{}][SQL] {}", start, sql);
        T result = jdbcTemplate.queryForObject(sql, requiredType, args);
        logger.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
        return result;
    }

    public Map<String, Object> queryForMap(String sqlKey, Object...args) throws DataAccessException {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        long start = new Date().getTime();
        logger.debug("[{}][SQL] {}", start, sql);
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, args);
            logger.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return result;
        } catch(Exception e) {
            if (e instanceof EmptyResultDataAccessException) {
                EmptyResultDataAccessException erdae = (EmptyResultDataAccessException) e;
                logger.debug("None record has been found");
            } else if (e instanceof IncorrectResultSizeDataAccessException) {
                IncorrectResultSizeDataAccessException e2 = (IncorrectResultSizeDataAccessException) e;
                logger.debug("Expect {} record, but actual size is {}", e2.getExpectedSize(), e2.getActualSize());
            }
            throw e;
        }
    }

    public List<Map<String, Object>> queryForList(String sqlKey, int maxRowSize, Object...args) throws DataAccessException {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        long start = new Date().getTime();
        logger.debug("[{}][SQL] {}", start, sql);
        return jdbcTemplate.query(sql, args, rs -> {
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }
            List<Map<String, Object>> list = new ArrayList<>();
            int num = 1;
            int max = (maxRowSize == 0 ? 500: maxRowSize);
            while(rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for(String column : columns) {
                    row.put(column, rs.getObject(column));
                }
                list.add(row);
                num ++;
                if (max > 0 && num > max)
                    break;
            }
            logger.debug("[ROWS] " + list.size());
            logger.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return list;
        });
    }

    public Map<String, Object> queryForPage(String sqlKey, int maxRowSize, int page, Object...args) throws DataAccessException {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        long start = new Date().getTime();
        logger.debug("[{}][SQL] {}", start, sql);
        return jdbcTemplate.query(sql, args, rs -> {
            Map<String, Object> result = new HashMap<>();
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }
            result.put("columns", columns);

            // 需要分页信息
            int pageIndex = Math.max(page, 0);
            int pageSize = Math.min(maxRowSize, 500);
            // 获得记录总数
            rs.last();
            int totalCount = rs.getRow();
            // 构建分页内容
            int pageCount = 0;
            if (totalCount > 0) {
                if (totalCount % pageSize == 0)
                    pageCount = totalCount / pageSize;
                else
                    pageCount = (totalCount / pageSize) + 1;
            }
            if (pageCount == 0)
                pageIndex = 0;
            else {
                // 当前页编码，从0开始，如果传的值为Integer.MAX_VALUE为最后一页。 如果当前页超过总页数，也表示最后一页。
                if (pageIndex == Integer.MAX_VALUE || pageIndex >= pageCount) {
                    pageIndex = pageCount - 1;
                }
            }
            result.put("pageSize", pageSize);
            result.put("pageCount", pageCount);
            result.put("pageIndex", pageIndex);
            result.put("totalCount", totalCount);
            // 获取当前记录集的字段数据
            List<Map<String, Object>> list = new ArrayList<>();
            if (totalCount > pageIndex * pageSize) {
                rs.absolute(pageIndex * pageSize + 1);
                int rowNum = 0;
                do {
                    if (rowNum >= pageSize)
                        break;

                    Map<String, Object> row = new HashMap<>();
                    for(String column : columns) {
                        row.put(column, rs.getObject(column));
                    }
                    list.add(row);
                    rowNum++;
                } while (rs.next());
            }
            logger.debug("[ROWS] " + list.size());
            result.put("fields", list);
            logger.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return result;
        });
    }

    public int execute(String sqlKey, Object...args) throws DataAccessException {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        long start = new Date().getTime();
        logger.debug("[{}][SQL] {}", start, sql);
        int result = jdbcTemplate.update(sql, args);
        logger.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
        return result;
    }

    public ExecuteBatch getBatcher() {
        return new ExecuteBatch(sqlTemplateManager, jdbcTemplate, transactionManager);
    }

}
