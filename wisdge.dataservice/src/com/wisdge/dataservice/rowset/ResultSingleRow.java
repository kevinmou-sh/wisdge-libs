package com.wisdge.dataservice.rowset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;

/**
 * 用于保存数据库查询集合ResultSet的数据记录，只包含一条查询结果记录
 * 
 * @author Kevin MOU
 * @version 1.0.0.20120414
 * @see IResultRow
 */
public class ResultSingleRow implements Serializable, IResultRow {
	private static final long serialVersionUID = -3249614818213400416L;
	@XmlElement(nillable = true)
	private List<RowMeta> metas;
	private Object[] singleRow;

	public ResultSingleRow() {
		metas = new ArrayList<RowMeta>();
	}

	/**
	 * 构造方法
	 * 
	 * @param metaList
	 *            字段信息列表
	 * @param objects
	 *            字段值堆栈
	 */
	public ResultSingleRow(List<RowMeta> metaList, Object[] objects) {
		this.metas = metaList;
		singleRow = objects;
	}

	/*
	 * (non-Javadoc)
	 * @see com.wisdge.sundial.webservices.rowset.IResultRow#addMeta(com.wisdge.sundial.webservices.rowset.RowMeta)
	 */
	public void addMeta(RowMeta meta) {
		metas.add(meta);
	}

	/*
	 * (non-Javadoc)
	 * @see com.wisdge.sundial.webservices.rowset.IResultRow#getMetaList()
	 */
	public List<RowMeta> getMetas() {
		return metas;
	}

	/*
	 * (non-Javadoc)
	 * @see com.wisdge.sundial.webservices.rowset.IResultRow#getMetaSize()
	 */
	public int getMetasSize() {
		return metas.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.wisdge.sundial.webservices.rowset.IResultRow#getMeta(int)
	 */
	public RowMeta getMeta(int index) {
		return metas.get(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.wisdge.sundial.webservices.rowset.IResultRow#getMetaString()
	 */
	public String getMetaString() {
		StringBuffer buffer = new StringBuffer();
		for (RowMeta meta : this.getMetas()) {
			buffer.append("[").append(meta.getFieldName()).append("]");
		}
		return buffer.toString();
	}

	/**
	 * 设置行数据记录内容
	 * 
	 * @param objects
	 */
	public void setRow(Object[] objects) {
		this.singleRow = objects;
	}

	/**
	 * 获得行数据内容
	 * 
	 * @return Object[]数据堆栈
	 */
	public Object[] getRow() {
		return this.singleRow;
	}

	/**
	 * 根据字段名称获得在记录中的字段序号
	 * 
	 * @param fieldName
	 *            String 字段名称
	 * @return int 字段序号
	 */
	public int getColumnIndexByName(String fieldName) {
		for (int i = 0; i < metas.size(); i++) {
			RowMeta meta = metas.get(i);
			if (meta.getFieldName().equalsIgnoreCase(fieldName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获得指定序号的字段内容
	 * 
	 * @param index
	 *            字段序号
	 * @return Object
	 */
	public Object getColumn(int index) {
		if (index == -1 || index >= singleRow.length) {
			return null;
		}

		return singleRow[index];
	}

	/**
	 * 获得指定名称的字段内容
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Object
	 */
	public Object getColumn(String fieldName) {
		return getColumn(getColumnIndexByName(fieldName));
	}

	/**
	 * 获得指定字段名称的内容，返回为Boolean对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Boolean，返回对象
	 */
	public Boolean getBoolean(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Boolean) {
			return (Boolean) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为Boolean值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return boolean值
	 */
	public boolean getBooleanValue(String fieldName) {
		Boolean result = getBoolean(fieldName);
		if (result == null) {
			return false;
		}

		return result.booleanValue();
	}

	/**
	 * 获得指定字段名称的内容，返回为Integer对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Integer对象
	 */
	public Integer getInt(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Integer) {
			return (Integer) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为Integer值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Integer值
	 */
	public int getIntValue(String fieldName) {
		Integer result = getInt(fieldName);
		if (result == null) {
			return 0;
		}

		return result.intValue();
	}

	/**
	 * 获得指定字段名称的内容，返回为String对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return String对象
	 */
	public String getString(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof String) {
			return (String) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为Float对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Float对象
	 */
	public Float getFloat(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Float) {
			return (Float) object;
		} else {
			return null;
		}

	}

	/**
	 * 获得指定字段名称的内容，返回为float值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return float值
	 */
	public float getFloatValue(String fieldName) {
		Float result = getFloat(fieldName);
		if (result == null) {
			return 0f;
		}

		return result.floatValue();
	}

	/**
	 * 获得指定字段名称的内容，返回为Long对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Long对象
	 */
	public Long getLong(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Long) {
			return (Long) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为long值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return long值
	 */
	public long getLongValue(String fieldName) {
		Long result = getLong(fieldName);
		if (result == null) {
			return 0L;
		}

		return result.longValue();
	}

	/**
	 * 获得指定字段名称的内容，返回为Date对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Date对象
	 */
	public Date getDate(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Date) {
			return (Date) object;
		} else if (object instanceof Timestamp) {
			return new Date(((Timestamp) object).getTime());
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为BigDecimal对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return BigDecimal对象
	 */
	public BigDecimal getBigDecimal(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof BigDecimal) {
			return (BigDecimal) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为Double对象
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return Double对象
	 */
	public Double getDouble(String fieldName) {
		Object object = getColumn(fieldName);
		if (object instanceof Double) {
			return (Double) object;
		} else {
			return null;
		}
	}

	/**
	 * 获得指定字段名称的内容，返回为double值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return double值
	 */
	public double getDoubleValue(String fieldName) {
		Double result = getDouble(fieldName);
		if (result == null) {
			return 0;
		}

		return result.doubleValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (singleRow == null) {
			return String.format(this.getClass().getName() + "[MetaSize=%d][SingleRow=Null]", metas.size());
		} else {
			return String.format(this.getClass().getName() + "[MetaSize=%d][SingleRow is valid]", metas.size());
		}
	}

	
	/**
	 * @return 获得记录所有字段的MAP对象
	 */
	public Map<String, Object> getFields() {
		Map<String, Object> fields = new HashMap<String, Object>();
		for(int i=0; i<metas.size(); i++) {
			fields.put(metas.get(i).getFieldName(), singleRow[i]);
		}
		
		return fields;
	}
}
