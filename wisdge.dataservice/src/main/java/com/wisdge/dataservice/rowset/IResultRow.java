package com.wisdge.dataservice.rowset;

import java.util.List;

/**
 * 数据库查询行记录元数据操作接口
 * 
 * @author Kevin MOU
 * @version 1.0.1.20120414
 */
public interface IResultRow {

	/**
	 * 增加一个元数据信息
	 * 
	 * @param meta
	 *            元数据RowMeta对象
	 */
	public void addMeta(RowMeta meta);

	/**
	 * 获得某个元数据信息
	 * 
	 * @param index
	 *            元数据信息在行中的字段序列
	 * @return RowMeta对象
	 */
	public RowMeta getMeta(int index);

	/**
	 * 获得当前记录的元数据数量
	 * 
	 * @return int，元数据数量
	 */
	public int getMetasSize();

	/**
	 * 获得当前记录的元数据队列
	 * 
	 * @return List<RowMeta>队列
	 */
	public List<RowMeta> getMetas();

	/**
	 * 获得当前对象的数据记录元数据信息
	 * 
	 * @return String 元素信息
	 */
	public String getMetaString();
}
