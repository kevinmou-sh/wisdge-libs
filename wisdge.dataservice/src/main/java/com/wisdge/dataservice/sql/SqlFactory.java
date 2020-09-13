package com.wisdge.dataservice.sql;

import com.wisdge.dataservice.exceptions.ProcessSqlContextException;
import com.wisdge.dataservice.exceptions.SqlTemplateNullPointerException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SqlFactory {
    private JdbcTemplate jdbcTemplate;
    private PlatformTransactionManager transactionManager;
    private SqlTemplateManager sqlTemplateManager;
    private String dbType;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public SqlTemplateManager getSqlTemplateManager() {
        return sqlTemplateManager;
    }

    public void setSqlTemplateManager(SqlTemplateManager sqlTemplateManager) {
        this.sqlTemplateManager = sqlTemplateManager;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public SqlFactory() {

    }

    private String mergeWithFreemarker(String sql, Map<String, Object> parameters) throws ProcessSqlContextException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
        // Set the preferred charset template files are stored in. UTF-8 is a good choice in most applications:
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);
        // Wrap unchecked exceptions thrown during template processing into
        // TemplateException-s.
        cfg.setWrapUncheckedExceptions(true);

        StringTemplateLoader stl = new StringTemplateLoader();
        stl.putTemplate("Sql", sql);
        cfg.setTemplateLoader(stl);

        try(
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer write = new OutputStreamWriter(baos);
        ) {
            Template template = cfg.getTemplate("Sql");
            template.process(parameters, write);
            return new String(baos.toByteArray());
        } catch (Exception e) {
            throw new ProcessSqlContextException(e.getMessage());
        }
    }

    private String mergeWithVelocity(String sql, Map<String, Object> parameters) throws ProcessSqlContextException {
        VelocityEngine ve = new VelocityEngine();
        VelocityContext context = new VelocityContext();
        Iterator<String> iter = parameters.keySet().iterator();
        while(iter.hasNext()) {
            String key = iter.next();
            context.put(key, parameters.get(key));
        }
        try (
                StringWriter writer = new StringWriter();
        ) {
            ve.evaluate(context, writer, "SqlTemplate", sql);
            return writer.toString();
        } catch(Exception e) {
            throw new ProcessSqlContextException(e.getMessage());
        }
    }

    public String processSqlContext(String sqlKey, Map<String, Object> processContext) throws ProcessSqlContextException {
        SqlTemplate sqlTemplate = sqlTemplateManager.getTemplate(sqlKey);
        String sql = SqlTemplateManager.replaceVariables(sqlTemplate.getSql(), dbType);
        if (processContext == null)
            processContext = new HashMap<>();
        if ("freemarker".equalsIgnoreCase(sqlTemplate.getProcess())) {
            // process with freemarker
            sql =  mergeWithFreemarker(sql, processContext);
        } else if ("velocity".equalsIgnoreCase(sqlTemplate.getProcess())) {
            // process with velocity
            sql = mergeWithVelocity(sql, processContext);
        }
        return sql;
    }

    public <T> T queryForObject(String sqlKey, Class<T> requiredType, Object...args) throws DataAccessException, ProcessSqlContextException, IllegalAccessException, InstantiationException {
        return queryForObject(sqlKey, null, requiredType, args);
    }

    public <T> T queryForObject(String sqlKey, Map<String, Object> processContext, Class<T> requiredType, Object...args) throws DataAccessException, ProcessSqlContextException, IllegalAccessException, InstantiationException {
        long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);

        if (IRowMapper.class.isAssignableFrom(requiredType)) {
            T result = requiredType.newInstance();
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, args);
            ((IRowMapper) result).rowMap(map);
            return (T) result;
        } else {
            T result = jdbcTemplate.queryForObject(sql, requiredType, args);
            log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return result;
        }
    }

    public Map<String, Object> queryForMap(String sqlKey, Object...args) throws DataAccessException, ProcessSqlContextException {
        return queryForMap(sqlKey, null, args);
    }

    public Map<String, Object> queryForMap(String sqlKey, Map<String, Object> processContext, Object...args) throws DataAccessException, ProcessSqlContextException {
        long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, args);
            log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return result;
        } catch(Exception e) {
            if (e instanceof EmptyResultDataAccessException) {
                EmptyResultDataAccessException erdae = (EmptyResultDataAccessException) e;
                log.debug("None record has been found");
            } else if (e instanceof IncorrectResultSizeDataAccessException) {
                IncorrectResultSizeDataAccessException e2 = (IncorrectResultSizeDataAccessException) e;
                log.debug("Expect {} record, but actual size is {}", e2.getExpectedSize(), e2.getActualSize());
            }
            throw e;
        }
    }

    public List<Map<String, Object>> queryForList(String sqlKey, int maxRowSize, Object...args) throws DataAccessException, ProcessSqlContextException {
        return queryForList(sqlKey, null, maxRowSize, args);
    }

    public List<Map<String, Object>> queryForList(String sqlKey, Map<String, Object> processContext, int maxRowSize, Object...args) throws DataAccessException, ProcessSqlContextException {
        long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);
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
            log.debug("[ROWS] " + list.size());
            log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return list;
        });
    }
    public Pagination queryForPage(String sqlKey, int maxRowSize, int page, Object...args) throws DataAccessException, ProcessSqlContextException {
        return queryForPage(sqlKey, null, maxRowSize, page, args);
    }

    public Pagination queryForPage(String sqlKey, Map<String, Object> processContext, int maxRowSize, int page, Object...args) throws DataAccessException, ProcessSqlContextException {
        final long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);

        String countSql = "SELECT COUNT(*) FROM (" + sql + ") PAGER_COUNT_ALIAS";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class, args);
        // 需要分页信息
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(maxRowSize, 500);
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
        final Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageCount(pageCount);
        pagination.setPageIndex(pageIndex);
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
            log.debug("[ROWS] " + list.size());
            pagination.setFields(list);
            log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
            return pagination;
        });
    }

    public int execute(String sqlKey, Object...args) throws DataAccessException, ProcessSqlContextException {
        return execute(sqlKey, null, args);
    }

    public int execute(String sqlKey, Map<String, Object> processContext) throws DataAccessException, ProcessSqlContextException {
        long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);
        List<Object> placeholders = new ArrayList<>();
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (processContext.containsKey(key))
                placeholders.add(processContext.get(key));
            else
                throw new SqlTemplateNullPointerException(key + " has not injected");
        }
        sql = sql.replaceAll("@(\\w+)", "?");
        int result = jdbcTemplate.update(sql, placeholders.toArray());
        log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
        return result;
    }

    public int execute(String sqlKey, Map<String, Object> processContext, Object...args) throws DataAccessException, ProcessSqlContextException {
        long start = new Date().getTime();
        String sql = processSqlContext(sqlKey, processContext);
        log.debug("[{}][SQL] {}", start, sql);
        int result = jdbcTemplate.update(sql, args);
        log.debug("[{}]Query took {}'ms", start, new Date().getTime() - start);
        return result;
    }

    public ExecuteBatch getBatcher() {
        return new ExecuteBatch(this);
    }

    public void addBatch(ExecuteBatch batch, String sqlKey, Object...args) throws ProcessSqlContextException {
        addBatch(batch, sqlKey, null, args);
    }

    public void addBatch(ExecuteBatch batch, Map<String, Object> processContext, String sqlKey, Object...args) throws ProcessSqlContextException {
        batch.add(processSqlContext(sqlKey, processContext), args);
    }

    @Test
    public void test() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(SqlFactory.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            List<PropertyDescriptor> descriptors = CollectionUtils.arrayToList(propertyDescriptors);
            log.info("PropertyDescriptors {}", descriptors.size());
            for(PropertyDescriptor property : descriptors) {
                log.info(property.getName());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
