package com.wisdge.dataservice.rowset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

/**
 * 分页构造器的基类
 * 
 * @author Kevin.MOU
 */
public class JdbcPage<T> {
	/**
	 * 当前页的记录集合
	 */
	protected List<T> elements;
	/**
	 * 分页记录数
	 */
	protected int pageSize = 1;
	/**
	 * 当前页下标
	 */
	protected int pageIndex = 0;
	/**
	 * 总页数
	 */
	protected int pageCount = 0;
	/**
	 * 总记录数
	 */
	protected int totalCount = 0;

	/**
	 * 判断当前页是否首页
	 * 
	 * @return boolean
	 */
	public boolean isFirst() {
		return this.pageIndex == 0;
	}

	/**
	 * 判断当前页是否末页
	 * 
	 * @return boolean
	 */
	public boolean isLast() {
		return this.pageIndex >= this.pageCount;
	}

	/**
	 * 判断是否有下一页
	 * 
	 * @return boolean
	 */
	public boolean hasNext() {
		return this.pageCount > this.pageIndex;
	}

	/**
	 * 判断是否有上一页
	 * 
	 * @return boolean
	 */
	public boolean hasPrevious() {
		return this.pageIndex > 0;
	}

	/**
	 * 取得当前页下的所有数据对象
	 * 
	 * @return List
	 */
	public List<T> getElements() {
		return this.elements;
	}

	/**
	 * 取得当前数据查询返回的所有记录总数
	 * 
	 * @return int
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 取得当前每页记录数
	 * 
	 * @return int
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 取得当前页的页码，第一页的下标为0
	 * 
	 * @return int
	 */
	public int getPageIndex() {
		return this.pageIndex;
	}

	/**
	 * 取得当前数据库查询返回的总页数
	 * 
	 * @return int
	 */
	public int getPageCount() {
		return this.pageCount;
	}

	/**
	 * 取得当前页的第一条记录在所有查询结果集中的位置
	 * 
	 * @return int
	 */
	public int getPageStartIdxOfScroll() {
		return this.pageIndex * this.pageSize;
	}

	/**
	 * 取得当前页的最后一条记录在所有查询结果集中的位置
	 * 
	 * @return int
	 */
	public int getPageEndIdxOfScroll() {
		int fullPage = getPageStartIdxOfScroll() + getPageSize();
		return (this.totalCount < fullPage) ? this.totalCount : fullPage;
	}

	protected void initialize() {
		if (this.pageSize == 0)
			this.pageSize = 1;

		this.pageCount = (int) Math.ceil((float) this.totalCount / (float) this.pageSize);

		if (this.pageIndex < 0)
			this.pageIndex = 0;
		else if (this.pageIndex > this.pageCount - 1)
			this.pageIndex = this.pageCount - 1;
	}

	/**
	 * JdbcPage构造方法
	 * @param resultSet 当前的数据库查询
	 * @param pageSize 分页的数量
	 * @param pageIndex 查询的页码（第一页从0开始）
	 * @param mapper 对查询结果记录进行Mapper转化的类
	 * @see RowMapper
	 */
	public JdbcPage(ResultSet resultSet, int pageSize, int pageIndex, RowMapper<T> mapper) {
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.elements = new ArrayList<T>();

		if (resultSet == null) {
			this.totalCount = this.pageIndex = this.pageCount = 0;
			return;
		}

		try {
			if (! resultSet.last()) {
				this.totalCount = this.pageIndex = this.pageCount = 0;
				return;
			}
			this.totalCount = resultSet.getRow(); // The first row is number 1 in resultSet.getRow() method
			initialize();
			if (this.totalCount > this.pageIndex * this.pageSize) {
				resultSet.absolute(this.pageIndex * this.pageSize + 1);
				int rowNum = 0;
				do {
					T bean = mapper.mapRow(resultSet, rowNum);
					elements.add(bean);
					rowNum++;
					if (rowNum >= this.pageSize)
						break;
				} while (resultSet.next());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
