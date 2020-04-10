package com.wisdge.dataservice;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultSetBean {
	public void makeBean(ResultSet resultSet) throws SQLException;
}
