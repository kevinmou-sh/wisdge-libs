package com.wisdge.dataservice.sql.page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

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
        final Pagination pagination = new Pagination(totalRows, pageIndex, pageSize);
        int offset = pagination.getPageIndex() * pagination.getPageSize();
        String pageSql = PageHelper.getLimitString(sql, offset, pageSize);
        log.debug("Query page: {}", pageSql);
        jdbcTemplate.query(pageSql, params, rch -> {
            // 获取当前记录集的字段数据
            List<String> columns = new ArrayList<>();
            ResultSetMetaData rsmd = rch.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                columns.add(rsmd.getColumnLabel(column).toUpperCase());
            }

            int size = columns.size();
            List<Object[]> fields = new ArrayList<>();
            while(rch.next()) {
                Object[] record = new Object[size];
                for(int i = 0; i < size; i ++)
                    record[i] = rch.getObject(i + 1);
                fields.add(record);
            }

            pagination.setColumns(columns);
            pagination.setFields(fields);
        });
        return pagination;
    }
}
