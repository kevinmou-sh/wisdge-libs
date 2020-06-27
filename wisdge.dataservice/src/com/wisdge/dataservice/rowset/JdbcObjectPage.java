package com.wisdge.dataservice.rowset;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * 分页构造器，输出元素为Object[]
 * 
 * @author Kevin.MOU
 * @since 1.1.1
 */
public class JdbcObjectPage extends JdbcPage<Object[]> {

	/**
	 * JdbcObjectPage构造方法
	 * @param resultSet 当前的数据库查询
	 * @param pageSize 分页的数量
	 * @param pageIndex 查询的页码（第一页从0开始）
	 * @see JdbcPage
	 */
	public JdbcObjectPage(ResultSet resultSet, int pageSize, int pageIndex) {
		super(resultSet, pageSize, pageIndex, new ObjectsRowMapper());
	}

}

class ObjectsRowMapper implements RowMapper<Object[]> {
	@Override
	public Object[] mapRow(ResultSet resultSet, int index) throws SQLException {
		Object[] objects = new Object[resultSet.getMetaData().getColumnCount()];
		for(int i=0; i<objects.length; i++) {
			objects[i] = resultSet.getObject(i+1);
		}
		return objects;
	}
}
