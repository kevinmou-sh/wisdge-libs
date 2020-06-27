package com.wisdge.dataservice.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.util.Assert;

public class UpdateablePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
	private String sql;
	
	public UpdateablePreparedStatementCreator(String sql) {
		Assert.notNull(sql, "SQL must not be null");   
        this.sql = sql;
	}
	
	@Override
	public String getSql() {
		return this.sql;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		return connection.prepareStatement(this.sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

}
