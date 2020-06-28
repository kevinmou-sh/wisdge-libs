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
	private List<String> sqls;
	private Map<String, SqlTemplate> sqlTemplates = new HashMap<>();

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

						sqlTemplates.put(sqlKey, new SqlTemplate(element.getTextTrim(), element.attributeValue("process")));
					}
				}
				logger.debug("load sql templates {}, {} records", sqlFile, sqlTemplates.size());
			} catch (Exception e) {
				logger.error("Load sql templates {} failed", sqlFile, e);
			}
		}
	}


	public SqlTemplate getTemplate(String sqlKey) throws SqlTemplateNullPointerException {
		return getTemplate(sqlKey, "MYSQL");
	}

	public SqlTemplate getTemplate(String sqlKey, String dbType) throws SqlTemplateNullPointerException {
		if (StringUtils.isEmpty(sqlKey))
			throw new SqlTemplateNullPointerException(sqlKey);

		if (!StringUtils.isEmpty(dbType)) {
			String fullKey = sqlKey + "." + dbType.toUpperCase();
			SqlTemplate sql = sqlTemplates.get(fullKey);
			if (sql != null)
				return sql;
		}

		if (!sqlTemplates.containsKey(sqlKey))
			throw new SqlTemplateNullPointerException(sqlKey);

		return sqlTemplates.get(sqlKey);
	}

	public static String replaceVariables(String sql, String dbType) {
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
}
