package com.wisdge.dataservice.rowset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对标准数据库查询结果 java.sql.ResultSet进行处理的类，将ResultSet对象转化为可以操作元数据的IResultRow对象。 <code>
 * <br/>
 * 通常将ResultSet对象转化为ResultRowSet或者ResultSingleRow对象，以便通过WebService接口被远端客户端读取。
 * 
 * @author Kevin MOU
 * @version 1.0.20121128
 */
public class JDBCRowSetMap {
	private static final Logger logger = LoggerFactory.getLogger(JDBCRowSetMap.class);
	
	/**
	 * 映射数据库查询结果ResultSet到ResultRowSet对象，并且根据传入值pageSize和pageCursor进行分页处理。
	 * 
	 * @param rs
	 *            需要处理的数据库查询结果ResultSet对象
	 * @param pageSize
	 *            当前分页的页面记录数。如果等于或小于0则不做分页处理
	 * @param pageCursor
	 *            当前分页的页面下标。分页下标从0开始计算，如果传的值为Integer.MAX_VALUE或者小于0表示获取最后一页。 如果当前页超过总页数，也表示最后一页。
	 * @return 转化后的ResultRowSet对象
	 * @throws SQLException
	 * @see ResultRowSet
	 */
	public static ResultRowSet mapResultRowSet(ResultSet rs, int pageSize, int pageIndex) throws SQLException {
		// 获得记录总数
		rs.last();
		int totalCount = rs.getRow();

		if (pageSize <= 0) { // 不使用分页处理
			pageIndex = 0;
			pageSize = totalCount;
		}

		// 构建ResultRowSet分页内容
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
			// 当前页编码，从0开始，如果传的值为Integer.MAX_VALUE或者小于0表示获取最后一页。 如果当前页超过总页数，也表示最后一页。
			if (pageIndex < 0 || pageIndex == Integer.MAX_VALUE || pageIndex >= pageCount) {
				pageIndex = pageCount - 1;
			}
		}

		ResultRowSet rowSet = new ResultRowSet(pageSize, pageIndex, totalCount, pageCount);
		// 获取当前记录集的字段数据
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int column = 1; column <= rsmd.getColumnCount(); column++) {
			RowMeta meta = getMeta(rsmd, column);
			rowSet.addMeta(meta);
		}

		if (totalCount > pageIndex * pageSize) {
			rs.absolute(pageIndex * pageSize + 1);
			int rowNum = 0;
			do {
				if (rowNum >= pageSize)
					break;
				Object[] object = getRowObjects(rowSet, rs);
				rowSet.addRow(object);
				rowNum++;
			} while (rs.next());
		}

		//System.out.println("JDBCRowSetMap.mapResultRowSet.PageSize = " + rowSet.getPageSize());
		return rowSet;
	}

	/**
	 * 映射数据库查询结果ResultSet到ResultSingleRow对象，该方法取数据库查询结果ResultSet的第一条记录进行转化。
	 * 
	 * @param rs
	 *            需要处理的数据库查询结果ResultSet对象
	 * @return 转化后的ResultSingleRow对象
	 * @throws SQLException
	 * @see ResultSingleRow
	 */
	public static ResultSingleRow mapResultSingleRow(ResultSet rs) throws SQLException {
		ResultSingleRow singleResult = new ResultSingleRow();
		ResultSetMetaData rsmd;

		rsmd = rs.getMetaData();
		for (int column = 1; column <= rsmd.getColumnCount(); column++) {
			RowMeta meta = getMeta(rsmd, column);
			singleResult.addMeta(meta);
		}

		if (rs.next()) {
			Object[] object = getRowObjects(singleResult, rs);
			singleResult.setRow(object);
		} else
			return null;

		return singleResult;
	}

	/**
	 * 从数据库表结构元数据ResultSetMetaData对象中获得某字段的RowMeta对象。ResultSetMetaData对象中包含数据库表结构的所有字段元信息，RowMeta只是对某个字段的描述信息。
	 * 
	 * @param rsmd
	 *            数据库表结构元素据对象 ResultSetMetaData
	 * @param column
	 *            目标字段的列号
	 * @return RowMeta对象
	 * @throws SQLException
	 * @see ResultSetMetaData
	 */
	public static RowMeta getMeta(ResultSetMetaData rsmd, int column) throws SQLException {
		RowMeta meta = new RowMeta();
		meta.setColumn(column);
		meta.setFieldName(rsmd.getColumnLabel(column).toUpperCase());
		meta.setTypeName(rsmd.getColumnTypeName(column));
		meta.setClassName(rsmd.getColumnClassName(column));
		meta.setPercision(rsmd.getPrecision(column));
		meta.setScale(rsmd.getScale(column));
		meta.setNullable(rsmd.isNullable(column) == ResultSetMetaData.columnNoNulls ? false : true);
		return meta;
	}

	private static Object[] getRowObjects(IResultRow row, ResultSet rs) throws SQLException {
		Object[] objects = new Object[row.getMetasSize()];
		for (int idx = 0; idx < row.getMetasSize(); idx++) {
			RowMeta meta = row.getMeta(idx);
			try {
				objects[idx] = rs.getObject(meta.getFieldName());
			} catch(Exception e) {
				if (rs.wasNull()) {
					objects[idx] = null;
				} else {
					logger.error("Unrecognise Meta: " + meta);
					logger.error(e.getMessage(), e);
				}
			}
		}
		return objects;
	}
}
