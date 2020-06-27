package com.wisdge.dataservice.rowset;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EntityBean implements IResultSetBean {
	/**
	 * 获得ResultSet一个字段的非空的字符串
	 * 
	 * @param rs
	 *            ResultSet对象
	 * @param columnName
	 *            字段名称
	 * @return 非空字符串
	 * @throws SQLException
	 */
	protected String notNullString(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return notNullString(s);
	}

	/**
	 * 获得一个非空的字符串
	 * 
	 * @param str
	 *            源字符串
	 * @return 非空字符串
	 */
	protected String notNullString(String str) {
		return str == null ? "" : str.trim();
	}

	/**
	 * 获得一个非空的大数字，默认为0
	 * 
	 * @param value
	 *            BigDecimal
	 * @return BigDecimal
	 */
	protected BigDecimal notNullBigDecimal(BigDecimal value) {
		return value == null ? new BigDecimal(0) : value;
	}

	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

}
