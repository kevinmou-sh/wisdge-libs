package com.wisdge.dataservice.utils;

import com.wisdge.dataservice.sql.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class JdbcUtils {
    public static Pagination queryForPage(JdbcTemplate jdbcTemplate, String sql, int pageSize, int page, Object...args) throws DataAccessException {
        final long start = new Date().getTime();
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") PAGER_COUNT_ALIAS";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class, args);
        // 构建分页内容
        int pageCount = 0;
        if (totalCount > 0) {
            if (totalCount % pageSize == 0)
                pageCount = totalCount / pageSize;
            else
                pageCount = (totalCount / pageSize) + 1;
        }
        if (pageCount == 0)
            page = 0;
        else {
            // 当前页编码，从0开始，如果传的值为Integer.MAX_VALUE为最后一页。
            // 如果当前页超过总页数，也表示最后一页。
            if (page == Integer.MAX_VALUE || page >= pageCount) {
                page = pageCount - 1;
            }
        }
        final Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageCount(pageCount);
        pagination.setPageIndex(page);
        pagination.setTotalCount(totalCount);

        String realSql = sql + " limit "+ pagination.getPageSize() + " offset " + (pagination.getPageSize() * pagination.getPageIndex());
        return jdbcTemplate.query(realSql, args, rs -> {
            Map<String, Object> result = new HashMap<>();
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }
            pagination.setColumns(columns);

            // 获取当前记录集的字段数据
            List<Map<String, Object>> list = new ArrayList<>();
            while(rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for(String column : columns) {
                    row.put(column, rs.getObject(column));
                }
                list.add(row);
            }
            pagination.setFields(list);
            log.debug("Query {} records, took {}'ms", list.size(), new Date().getTime() - start);
            return pagination;
        });
    }

    public static String getClobString(Clob clob) throws SQLException, IOException {
        if (clob == null)
            return "";

        StringBuilder buf = new StringBuilder();
        BufferedReader is = new BufferedReader(clob.getCharacterStream());
        while (true) {
            String str = is.readLine();
            if (str == null) {
                break;
            }
            buf.append(str + "\n");
        }
        is.close();
        return buf.toString();
    }
}
