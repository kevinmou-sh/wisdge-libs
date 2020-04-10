package com.wisdge.dataservice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import com.wisdge.dataservice.rowset.JDBCRowSetMap;
import com.wisdge.dataservice.rowset.ResultRowSet;

public class DataService {
	private static final Log logger = LogFactory.getLog(DataService.class);
	protected JdbcTemplate jdbc;
	protected SqlTemplateManager sql;
	protected String sqlTemplate;

	public String getSqlTemplate() {
		return sqlTemplate;
	}

	public void setSqlTemplate(String sqlTemplate) {
		this.sqlTemplate = sqlTemplate;
	}

	/**
	 * 获得springframework jdbcTemplate
	 * 
	 * @return JdbcTemplate
	 */
	public JdbcTemplate getJdbc() {
		return jdbc;
	}

	@Autowired
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * 获得SQL模板管理器
	 * 
	 * @return SqlQueryTemplateManager
	 */
	public SqlTemplateManager getSql() {
		if (sql == null) {
			if (sqlTemplate == null) {
				logger.error("Have not defined any sql template resource.");
				return null;
			}
			sql = new SqlTemplateManager();
			List<String> resources = new ArrayList<String>();
			resources.add(sqlTemplate);
			sql.setResources(resources);
			sql.initialize();
		}
		return sql;
	}

	@Autowired
	public void setSql(SqlTemplateManager sql) {
		this.sql = sql;
	}

	/**
	 * 获得SQL模板器重定义的SQL语句
	 * 
	 * @param templateName
	 * @return String
	 */
	public String getTemplate(String templateName) throws NullPointerException {
		return getSql().getTemplate(templateName);
	}

	/**
	 * 执行Jdbc update
	 * 
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            SQL语句变量
	 * @return 执行结果
	 * @throws DataAccessException
	 */
	public int update(String sql, Object... args) throws DataAccessException {
		return jdbc.update(sql, args);
	}

	public void execute(String sql) throws DataAccessException {
		jdbc.execute(sql);
	}

	public int batchUpdate(String[] sql) throws DataAccessException {
		return jdbc.batchUpdate(sql).length;
	}

	public int batchUpdate(String sql, Object[] args) throws DataAccessException {
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (Object obj : args) {
			batchArgs.add(new Object[] { obj });
		}
		return batchUpdate(sql, batchArgs);
	}

	public int batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
		return jdbc.batchUpdate(sql, batchArgs).length;
	}

	public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
		try {
			return jdbc.queryForMap(sql, args);
		} catch(DataAccessException e) {
			if (e instanceof EmptyResultDataAccessException)
				return null;
			throw e;
		}
	}

	public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
		try {
			return jdbc.queryForObject(sql, rowMapper, args);
		} catch(DataAccessException e) {
			if (e instanceof EmptyResultDataAccessException)
				return null;
			throw e;
		}
	}

	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		try {
			return jdbc.queryForObject(sql, requiredType, args);
		} catch(DataAccessException e) {
			if (e instanceof EmptyResultDataAccessException)
				return null;
			throw e;
		}
	}

	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
		return jdbc.query(sql, rowMapper, args);
	}

	/**
	 * 通过分页进行JDBC查询，
	 * 
	 * @param pageSize
	 *            分页记录数
	 * @param pageIndex
	 *            页码下标（第一页为0）
	 * @param sql
	 *            数据库查询语句
	 * @param mapper
	 *            RowMapper转换器
	 * @param args
	 *            数据库查询变量
	 * @return JdbcPage分页器
	 */
	public <T> JdbcPage<T> queryForPage(final int pageSize, final int pageIndex, String sql, final RowMapper<T> mapper, Object... args) {
		return jdbc.query(new UpdateablePreparedStatementCreator(sql), new ArgumentPreparedStatementSetter(args), new ResultSetExtractor<JdbcPage<T>>() {
			@Override
			public JdbcPage<T> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				JdbcPage<T> page = new JdbcPage<T>(resultSet, pageSize, pageIndex, mapper);
				return page;
			}
		});
	}

	public ResultRowSet queryForPage(final int pageSize, final int pageIndex, String sql, Object... args) {
		return jdbc.query(new UpdateablePreparedStatementCreator(sql), new ArgumentPreparedStatementSetter(args), new ResultSetExtractor<ResultRowSet>() {
			@Override
			public ResultRowSet extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				return JDBCRowSetMap.mapResultRowSet(resultSet, pageSize, pageIndex);
			}
		});
	}

	public String createOraclePagingSql(String sql, int pageIndex, int pageSize) {
		int m = pageIndex * pageSize;
		int n = m + pageSize;
		return "select * from ( select row_.*, rownum rownum_ from ( " + sql + " ) row_ where rownum <= " + n + ") where rownum_ > " + m;
	}

	/**
	 * 通过分页进行JDBC查询，
	 * 
	 * @param pageSize
	 *            分页记录数
	 * @param pageIndex
	 *            页码下标（第一页为0）
	 * @param sql
	 *            数据库查询语句
	 * @param args
	 *            数据库查询变量
	 * @return JdbcObjectPage分页器
	 */
	public JdbcObjectPage queryForObjectPage(final int pageSize, final int pageIndex, String sql, Object... args) {
		return jdbc.query(new UpdateablePreparedStatementCreator(sql), new ArgumentPreparedStatementSetter(args), new ResultSetExtractor<JdbcObjectPage>() {
			@Override
			public JdbcObjectPage extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				JdbcObjectPage page = new JdbcObjectPage(resultSet, pageSize, pageIndex);
				return page;
			}
		});
	}

	/**
	 * @param sql
	 * @param elementType
	 * @param args
	 * @return
	 */
	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) {
		try {
			return jdbc.queryForList(sql, elementType, args);
		} catch (Exception e) {
			logger.error(e, e);
			return new ArrayList<T>();
		}
	}

	/**
	 * @param sql
	 * @param rowMapper
	 * @param args
	 * @return
	 */
	public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
		try {
			return jdbc.query(sql, rowMapper, args);
		} catch (Exception e) {
			logger.error(e, e);
			return new ArrayList<T>();
		}
	}

	/**
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
		return jdbc.queryForList(sql, args);
	}

}
