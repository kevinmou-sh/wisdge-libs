package com.wisdge.dataservice.ws;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.wisdge.dataservice.ws.convertors.MapAdapter;
import com.wisdge.dataservice.ws.convertors.MapInListAdapter;

/**
 * @deprecated 建议使用<{@link DataService2}接口，实现数据接口的封装。<br/>
 * 基于Web Service的用户接口
 * 
 * @author Kevin MOU
 * @version 1.0.0
 */
@WebService
public interface DataService {
	public static short SQLSERVER = 0;
	public static short ORACLE = 1;

	/**
	 * 查询记录到一个MAP集合中
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return Map<String, Object> 对象结果
	 */
	@XmlJavaTypeAdapter(MapAdapter.class)
	public Map<String, Object> queryForMap(String sql, Object... args);

	/**
	 * 查询记录结果为一个整数值
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return int，查询结果
	 */
	public int queryForInt(String sql, Object... args);

	/**
	 * 查询记录结果为一个长整数
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return long， 查询结果
	 */
	public long queryForLong(String sql, Object... args);

	/**
	 * 查询数据特定字段单一结果到对象
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param requiredType
	 *            查询结果的转换对象类
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return 查询结果对象
	 */
	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args);

	/**
	 * 查询记录结果为一个数据队列
	 * 
	 * <pre>
	 * 对象为List&lt;Map&lt;String, Object&gt;&gt;，每行记录为一个为MAP对象
	 * </pre>
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return List<Map<String, Object>> 查询结果队列
	 */
	@XmlJavaTypeAdapter(MapInListAdapter.class)
	public List<Map<String, Object>> queryForList(String sql, Object... args);

	/**
	 * @param sql
	 *            执行查询的SQL语句
	 * @param pageIndex
	 *            当前查询页的下标
	 * @param pageSize
	 *            每页的记录数
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return List<Map<String, Object>> 查询结果队列
	 */
	@XmlJavaTypeAdapter(MapInListAdapter.class)
	public List<Map<String, Object>> queryForPage(String sql, int pageIndex, int pageSize, Object... args);

	/**
	 * 执行一条SQL语句
	 * 
	 * @param sql
	 *            执行操作的SQL语句
	 * @param args
	 *            参数堆栈
	 * @return 执行成功的记录数
	 */
	public int update(String sql, Object... args);

	/**
	 * 批量执行更形SQL
	 * 
	 * @param sql
	 *            执行操作的SQL语句
	 * @param batchArgs
	 *            参数堆栈队列
	 * @return 执行成功的记录数
	 */
	public int batchUpdate(String sql, List<Object[]> batchArgs);

	/**
	 * 执行存储过程获得特定对象，对应SQLSERVER数据库
	 * 
	 * @param dbType
	 *            数据库的类型， 可以指定 SQLSERVER或者ORACLE。DataService2.SQLSERVER or DataService2.ORACLE
	 * @param procName
	 *            存储过程名称
	 * @param objType
	 *            结果对象的类型
	 * @param args
	 *            参数堆栈
	 * @return 执行结果对象
	 */
	public <T> T callForObject(final short dbType, final String procName, int objType, final Object... args);

	/**
	 * 执行存储过程获得结果集合
	 * 
	 * @param procName
	 *            存储过程名称
	 * @param args
	 *            参数堆栈
	 * @return 执行结果集合
	 */
	@XmlJavaTypeAdapter(MapInListAdapter.class)
	public List<Map<String, Object>> callForList(String procName, Object... args);

}
