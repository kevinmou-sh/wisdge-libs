package com.wisdge.dataservice.exceptions;

import org.apache.commons.lang3.StringUtils;

public class SqlTemplateNullPointerException extends NullPointerException {
	private static final long serialVersionUID = 1L;

	public SqlTemplateNullPointerException(String sqlKey) {
		super("模版" + (StringUtils.isEmpty(sqlKey) ? "<empty>" : sqlKey) + "不存在");
	}
}
