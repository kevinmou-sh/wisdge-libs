package com.wisdge.dataservice.exceptions;

import org.apache.commons.lang3.StringUtils;

public class JdbcNullPointerException extends NullPointerException {
	private static final long serialVersionUID = 1L;

	public JdbcNullPointerException(String dsKey) {
		super("JdbcTemplate " + (StringUtils.isEmpty(dsKey) ? "<empty>" : dsKey) + " has not defined");
	}
}
