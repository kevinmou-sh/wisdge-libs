package com.wisdge.dataservice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;

public class JDBCUtils {
	public static String getClobString(Clob clob) throws SQLException, IOException {
		if (clob == null)
			return "";
		
		StringBuilder buf = new StringBuilder();
		BufferedReader is = new BufferedReader(clob.getCharacterStream());
		while (true) {
			String str = is.readLine();
			if (str == null) {
				break;
			}
			buf.append(str + "\n");
		}
		is.close();
		return buf.toString();
	}
}
