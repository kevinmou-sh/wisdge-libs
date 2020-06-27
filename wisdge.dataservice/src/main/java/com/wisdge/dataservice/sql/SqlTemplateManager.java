package com.wisdge.dataservice.sql;

import com.wisdge.dataservice.exceptions.SqlTemplateNullPointerException;
import com.wisdge.utils.DomUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTemplateManager {
	private static final Logger logger = LoggerFactory.getLogger(SqlTemplateManager.class);
	private String dbType;
	private List<String> sqls;
	private Map<String, String> sqlTemplates = new HashMap<>();

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public List<String> getSqls() {
		return sqls;
	}

	public void setSqls(List<String> sqls) {
		this.sqls = sqls;
	}

	public void initialize() {
		if (sqls == null)
			sqls = new ArrayList<>();

		for(String sqlFile:sqls) {
			try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(sqlFile)) {
				Document document = DomUtils.parser(is);
				List<Node> nodes = document.selectNodes("//workset");
				for(Node node:nodes) {
					String workset = ((Element) node).attributeValue("name");
					logger.debug("Collection sql templates: {}", workset);
					List<Node> sqls = node.selectNodes("sql");
					for(Node sqlNode : sqls) {
						Element element = (Element) sqlNode;
						String sqlKey = workset + "." + element.attributeValue("name");
						String dbType = element.attributeValue("db");
						if (! StringUtils.isEmpty(dbType))
							sqlKey += "." + dbType.toUpperCase();
						sqlTemplates.put(sqlKey, element.getTextTrim());
					}
				}
			} catch (Exception e) {
				logger.error("Load sql templates {} failed", sqlFile, e);
			}
		}
	}

	public String getTemplate(String sqlKey) throws SqlTemplateNullPointerException {
		if (StringUtils.isEmpty(sqlKey))
			throw new SqlTemplateNullPointerException(sqlKey);

		if (! StringUtils.isEmpty(dbType)) {
			String fullKey = sqlKey + "." + dbType.toUpperCase();
			String sql = sqlTemplates.get(fullKey);
			if (sql != null)
				return sql;
		}

		if (! sqlTemplates.containsKey(sqlKey))
			throw new SqlTemplateNullPointerException(sqlKey);

		String sql = sqlTemplates.get(sqlKey);

		if (dbType.equalsIgnoreCase("MYSQL")) {
			return sql.replace("{C_GUID}", "UUID()")
					.replace("{C_NOW}", "NOW()");
		} else if (dbType.equalsIgnoreCase("SQLSERVER")) {
			return sql.replace("{C_GUID}", "NEWID()")
					.replace("{C_NOW}", "GETDATE()");
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			return sql.replace("{C_GUID}", "SYS_GUID()")
					.replace("'{C_NOW}'", "SYSDATE");
		}
		return sql;
	}

	@Test
	public void test() {
		
	}
}
