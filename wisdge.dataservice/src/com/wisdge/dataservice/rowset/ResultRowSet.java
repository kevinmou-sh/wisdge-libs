package com.wisdge.dataservice.rowset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;

/**
 * 用于保存数据库查询集合ResultSet的数据记录，包含若干条查询结果记录
 * 
 * @author Kevin MOU
 * @version 1.0.0.20121129
 * @see IResultRow
 * @see ResultSingleRow
 */
public class ResultRowSet implements Serializable, IResultRow {
	private static final long serialVersionUID = -3249614818213400416L;
	@XmlElement(nillable = true)
	private List<RowMeta> metas;
	@XmlElement(nillable = true)
	private List<Object[]> rows;
	@XmlElement(nillable = true)
	private int pageSize, pageIndex, totalCount, pageCount;

	public ResultRowSet() {
		metas = new ArrayList<RowMeta>();
		rows = new ArrayList<Object[]>();

		this.pageSize = 0;
		this.pageIndex = 0;
		this.totalCount = 0;
		this.pageCount = 0;
	}

	/**
	 * 构造方法
	 * 
	 * @param pageSize
	 *            分页的页面记录数
	 * @param pageCursor
	 *            当前页的下标，从0集数
	 * @param recordCount
	 *            记录总数
	 * @param pageCount
	 *            总页数
	 */
	public ResultRowSet(int pageSize, int pageIndex, int totalCount, int pageCount) {
		metas = new ArrayList<RowMeta>();
		rows = new ArrayList<Object[]>();

		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.totalCount = totalCount;
		this.pageCount = pageCount;
	}

	@Override
	public void addMeta(RowMeta meta) {
		metas.add(meta);
	}

	@Override
	public List<RowMeta> getMetas() {
		return metas;
	}

	@Override
	public int getMetasSize() {
		return metas.size();
	}

	@Override
	public RowMeta getMeta(int index) {
		return metas.get(index);
	}

	@Override
	public String getMetaString() {
		StringBuffer buffer = new StringBuffer();
		for (RowMeta meta : this.getMetas()) {
			buffer.append("[").append(meta.getFieldName()).append("]");
		}
		return buffer.toString();
	}

	/**
	 * 增加一行记录到队列中
	 * 
	 * @param objects
	 *            记录的字段内容
	 */
	public void addRow(Object[] objects) {
		rows.add(objects);
	}

	/**
	 * 获得所有记录
	 * 
	 * @return 记录对象Object[]列表
	 */
	public List<Object[]> getRows() {
		return rows;
	}
	
	/**
	 * 获得数据库记录及队列
	 * @return 记录对象Map&ltString, Object&gt;列表
	 */
	public List<Map<String, Object>> getElements() {
		List<Map<String, Object>> elements = new ArrayList<Map<String,Object>>();
		for(Object[] row : rows) {
			Map<String, Object> map = new HashMap<String, Object>();
			for(int i=0; i<row.length; i++) {
				map.put(metas.get(i).getFieldName(), row[i]);
			}
			elements.add(map);
		}
		return elements;
	}

	/**
	 * 获得记录集的总行数
	 * 
	 * @return int 记录集的总行数
	 */
	public int getRowsSize() {
		return rows.size();
	}

	/**
	 * 获得一行数据
	 * 
	 * @param index
	 *            数据记录在当前页中的行号
	 * @return Object[] 记录数据的对象数组
	 */
	public Object[] getRow(int index) {
		return rows.get(index);
	}

	/**
	 * 获得单行数据的记录对象
	 * 
	 * @param row
	 *            数据在当前页中的行号
	 * @return ResultSingleRow 数据的记录对象
	 */
	public ResultSingleRow getSingleRow(int row) {
		if (row < 0 || row >= rows.size()) {
			return null;
		}
		return new ResultSingleRow(metas, rows.get(row));
	}
	
	/**
	 * @return 获得所有ResultSingleRow记录的列表
	 */
	public List<ResultSingleRow> getRowList() {
		List<ResultSingleRow> rl = new ArrayList<ResultSingleRow>();
		for(int i=0; i<rows.size(); i++) {
			rl.add(getSingleRow(i));
		}
		
		return rl;
	}
	
	/**
	 * @return 获得ResultSingleRow记录集合的Iterator
	 */
	public Iterator<ResultSingleRow> iterator() {
		return getRowList().iterator();
	}

	/**
	 * 获得指定行号和字段序列的数据
	 * 
	 * @param row
	 *            数据在当前页中的行号
	 * @param columnIndex
	 *            数据所在的列序
	 * @return Object 数据对象
	 */
	public Object getColumn(int row, int columnIndex) {
		if (row >= rows.size()) {
			return null;
		}
		if (columnIndex >= metas.size()) {
			return null;
		}

		return rows.get(row)[columnIndex];
	}

	/**
	 * 获得指定行号和字段名的数据
	 * 
	 * @param row
	 *            数据在当前页中的行号
	 * @param columnName
	 *            数据字段名称
	 * @return Object 数据对象
	 */
	public Object getColumn(int row, String columnName) {
		if (row >= rows.size()) {
			return null;
		}

		for (int i = 0; i < metas.size(); i++) {
			if (metas.get(i).getFieldName().equalsIgnoreCase(columnName)) {
				return rows.get(row)[i];
			}
		}
		return null;
	}

	/**
	 * 取得某条记录的某个字段字符串值
	 * 
	 * @param row
	 *            被取值的Row下标
	 * @param columnName
	 *            被取值的字段名称
	 * @return String 字段串内容
	 */
	public String getString(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getString(columnName);
		}
		return null;
	}

	/**
	 * 获得指定行与列的字段Integer对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Integer对象
	 */
	public Integer getInt(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getInt(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段Float对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Float对象
	 */
	public Float getFloat(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getFloat(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段Long对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Long对象
	 */
	public Long getLong(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getLong(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段Double对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Double对象
	 */
	public Double getDouble(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getDouble(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段BigDecimal对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return BigDecimal对象
	 */
	public BigDecimal getBigDecimal(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getBigDecimal(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段Boolean对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Boolean对象
	 */
	public Boolean getBoolean(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getBoolean(columnName);
		}
		return null;
	}


	/**
	 * 获得指定行与列的字段Date对象
	 * 
	 * @param row
	 *            数据集合中的行
	 * @param columnName
	 *            数据集合中的列
	 * @return Date对象
	 */
	public Date getDate(int row, String columnName) {
		ResultSingleRow singleRow = this.getSingleRow(row);
		if (singleRow != null) {
			return singleRow.getDate(columnName);
		}
		return null;
	}

	/**
	 * 获得指定字段名称的记录中包含特定数据值在当前页中的行号
	 * 
	 * @param columnName
	 *            字段名称
	 * @param columnValue
	 *            被查找的数据值
	 * @return int 包含数据值的第一行在当前页中的行号，如果没有任何行包含该数据值，则返回-1
	 */
	public int getRowByColumnValue(String columnName, Object columnValue) {
		for (int i = 0; i < rows.size(); i++) {
			if (getColumn(i, columnName).equals(columnValue)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获得分页中的每页记录数（行数）
	 * 
	 * @return int 每页记录数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 获得当前页的页标
	 * 
	 * @return int 当前页的页标
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * 获得记录集分页的总页数
	 * 
	 * @return int 记录集分页的总页数
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 获得数据库数据表中的总记录数，该记录数是符合SQL查询条件的所有数据记录的总和
	 * 
	 * @return int 数据库数据表中的总记录数
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 是否分页记录集中的第一页
	 * 
	 * @return boolean 是第一页返回true， 否则返回false
	 */
	public boolean isFirstPage() {
		return pageIndex == 0;
	}

	/**
	 * 是否分页记录集中的最后一页
	 * 
	 * @return boolean 是最后一页返回true， 否则返回false
	 */
	public boolean isLastPage() {
		return pageIndex >= pageCount - 1;
	}

	/**
	 * 分页记录集中是否还有下一页
	 * 
	 * @return boolean 还有下一页返回true， 否则返回false
	 */
	public boolean hasNextPage() {
		return !isLastPage();
	}

	/**
	 * 分页记录集中是否还有上一页
	 * 
	 * @return boolean 还有上一页返回true， 否则返回false
	 */
	public boolean hasPreviousPage() {
		return pageIndex > 0;
	}

	/**
	 * 获得当前页面中的第一条记录在记录集中的行号
	 * 
	 * @return int 当前页面首记录在记录集中的行号
	 */
	public int getPageStartIndex() {
		return pageIndex * pageSize;
	}

	/**
	 * 获得当前页面中的最后一条记录在记录集中的行号
	 * 
	 * @return int 当前页面末记录在记录集中的行号
	 */
	public int getPageEndIndex() {
		int fullPage = getPageStartIndex() + getPageSize();
		return totalCount < fullPage ? totalCount : fullPage;
	}

	/**
	 * 输出一条记录为概要信息
	 * 
	 * @param row
	 *            记录在当前页面中的行号
	 * @return 指定行的内容概要信息
	 */
	public String toString(int row) {
		Object[] objects = rows.get(row);
		StringBuffer sb = new StringBuffer();
		sb.append("======================================================\n");
		for (int column = 0; column < metas.size(); column++) {
			RowMeta meta = metas.get(column);
			sb.append("[").append(meta.getFieldName()).append(" = ");
			sb.append(objects[column]).append("]\n");
		}
		sb.append("=======================================================\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return String.format(this.getClass().getName() + "[MetaSize=%d][RowSize=%d]", metas.size(), rows.size());
	}

}
