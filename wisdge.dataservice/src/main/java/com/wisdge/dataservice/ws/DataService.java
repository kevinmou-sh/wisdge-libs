package com.wisdge.dataservice.ws;

import java.util.List;
import javax.jws.WebService;
import com.wisdge.dataservice.rowset.ResultRowSet;
import com.wisdge.dataservice.rowset.ResultSingleRow;

/**
 * 基于Web Service的用户接口<br/>
 * 返回的查询对象使用封装对象 ResultRowSet和ResultSingleRow
 * 
 * @author Kevin MOU
 * @version 1.0.0
 * @see ResultRowSet
 * @see ResultSingleRow
 */
@WebService
public interface DataService {
	public static short SQLSERVER = 0;
	public static short ORACLE = 1;
	public static short MYSQL = 2;

	/**
	 * 查询记录到一个MAP集合中
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return ResultSingleRow 对象结果
	 * @see ResultSingleRow
	 * @see #queryForSet(String, Object...)
	 */
	public ResultSingleRow queryForMap(String sql, Object... args);

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
	 * 查询记录结果为ResultRowSet数据队列
	 * 
	 * @param sql
	 *            执行查询的SQL语句
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return ResultRowSet 查询结果队列
	 * @see ResultRowSet
	 * @see #queryForPage(String, int, int, Object...)
	 * @see #queryForMap(String, Object...)
	 */
	public ResultRowSet queryForSet(String sql, Object... args);

	/**
	 * @param sql
	 *            执行查询的SQL语句
	 * @param pageSize
	 *            每页的记录数，当pageSize=0时，不使用分页
	 * @param pageCursor
	 *            当前查询页的下标，从0行开始
	 * @param args
	 *            参数集合，对应到SQL语句中
	 * @return ResultRowSet 查询结果队列
	 * @see ResultRowSet
	 */
	public ResultRowSet queryForPage(String sql, int pageSize, int pageCursor, Object... args);

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
	 * 执行存储过程获得特定对象
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
	 * @return ResultRowSet 执行结果集合
	 * @see ResultRowSet
	 */
	public ResultRowSet callForSet(String procName, Object... args);

	/**
	 * 对WebService接口进行测试
	 * 
	 * @return String 测试结果字符串
	 */
	public String test();

}
