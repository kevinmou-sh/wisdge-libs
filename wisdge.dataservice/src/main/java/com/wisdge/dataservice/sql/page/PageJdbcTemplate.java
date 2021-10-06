package com.wisdge.dataservice.sql.page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PageJdbcTemplate {
    private JdbcTemplate jdbcTemplate;

    public PageJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Pagination queryForPage(PageInfo pageInfo, String sql, Object[] params) {
        int totalRows = jdbcTemplate.queryForObject(PageHelper.getCountString(sql), params, Integer.class);
        int pageSize = pageInfo.getPageSize();
        int pageIndex = pageInfo.getPageIndex();
        if (pageIndex < 1)
            pageIndex = 1;
        if (pageSize >= 5000) {
            log.warn("Page query size has overhead 5000, actually is {}", + pageSize);
        }
        Pagination pagination = new Pagination(totalRows, pageIndex, pageSize);
        int offset = (pagination.getPageIndex() - 1) * pagination.getPageSize();
        String pageSql = PageHelper.getLimitString(sql, offset, pageSize);
        // log.debug("Query page: {}", pageSql);
        jdbcTemplate.query(pageSql, params, rse -> {
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rse.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }
            pagination.setColumns(columns);

            // 获取当前记录集的字段数据
            List<Map<String, Object>> fields = new ArrayList<>();
            while(rse.next()) {
                Map<String, Object> row = new HashMap<>();
                for(String column : columns) {
                    row.put(column, rse.getObject(column));
                }
                fields.add(row);
            }
            pagination.setFields(fields);
        });
        return pagination;
    }

    public Pagination queryForPageLegacy(PageInfo pageInfo, String sql, Object[] params) {
        int totalRows = jdbcTemplate.queryForObject(PageHelper.getCountString(sql), params, Integer.class);
        int pageSize = pageInfo.getPageSize();
        int pageIndex = pageInfo.getPageIndex();
        if (pageIndex < 1)
            pageIndex = 1;
        if (pageSize >= 5000) {
            log.warn("Page query size has overhead 5000, actually is {}", + pageSize);
        }
        Pagination pagination = new Pagination(totalRows, pageIndex, pageSize);
        int offset = (pagination.getPageIndex() - 1) * pagination.getPageSize();
        String pageSql = PageHelper.getLimitString(sql, offset, pageSize);

        // log.debug("Query page: {}", pageSql);
        Map<String, List> rsResult = jdbcTemplate.query(pageSql, params, rse -> {
            Map<String, List> result = new HashMap<>();
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rse.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }
            result.put("columns", columns);

            int size = columns.size();
            List<Object[]> fields = new ArrayList<>();
            while(rse.next()) {
                Object[] record = new Object[size];
                for(int i = 0; i < size; i ++)
                    record[i] = rse.getObject(i + 1);
                fields.add(record);
            }
            result.put("fields", fields);

            log.info("<==\tTotal:{}", fields.size());
            return result;
        });

        pagination.setColumns(rsResult.get("columns"));
        pagination.setFields(rsResult.get("fields"));
        return pagination;
    }
}
