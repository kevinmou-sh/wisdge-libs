package com.wisdge.dataservice.sql;

import com.wisdge.dataservice.exceptions.SqlTemplateNullPointerException;
import com.wisdge.utils.DomUtils;
import com.wisdge.utils.FileUtils;
import com.wisdge.utils.FilenameUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class SqlTemplateManager {
	private String path;
	private List<String> sqls;
	private Map<String, SqlTemplate> sqlTemplates = new HashMap<>();
	private static final String PREFIX_CLASSPATH = "classpath:";

	public void initialize() {
		if (StringUtils.isEmpty(path))
			path = PREFIX_CLASSPATH + "/";
		path = path.trim();
		if (path.startsWith(PREFIX_CLASSPATH)) {
			initializeFromClasspath();
		} else {
			initializeFromOutside();
		}
	}

	private void initializeFromClasspath() {
		String packagePath = path.substring(10);
		for(String sqlFile:sqls) {
			String filepath = FilenameUtils.concat(packagePath, sqlFile);
			try (InputStream is = new ClassPathResource(filepath).getInputStream()) {
				loadTemplateFromInputStream(is);
				log.debug("Load sql templates from classpath:{}, {} records", filepath, sqlTemplates.size());
			} catch (Exception e) {
				log.error("Load sql templates {} failed", filepath, e);
			}
		}
	}

	private void initializeFromOutside() {
		for(String sqlFile:sqls) {
			String filepath = FilenameUtils.concat(path, sqlFile);
			try (InputStream is = new FileInputStream(filepath)) {
				loadTemplateFromInputStream(is);
				log.debug("Load sql templates {}, {} records", filepath, sqlTemplates.size());
			} catch (Exception e) {
				log.error("Load sql templates {} failed", filepath, e);
			}
		}
	}

	private void loadTemplateFromInputStream(InputStream is) throws Exception {
		Document document = DomUtils.parser(is);
		List<Node> nodes = document.selectNodes("//workset");
		for(Node node:nodes) {
			String workset = ((Element) node).attributeValue("name");
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
	}

	public SqlTemplate getTemplate(String sqlKey) throws SqlTemplateNullPointerException {
		return getTemplate(sqlKey, null);
	}

	public SqlTemplate getTemplate(String sqlKey, String dbType) throws SqlTemplateNullPointerException {
		if (StringUtils.isEmpty(sqlKey))
			throw new SqlTemplateNullPointerException(sqlKey);

		if (StringUtils.isNotEmpty(dbType)) {
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
			return sql.replace("{C_GUID}", "UUID()").replace("{C_NOW}", "NOW()");
		} else if (dbType.equalsIgnoreCase("MSSQL")) {
			return sql.replace("{C_GUID}", "NEWID()").replace("{C_NOW}", "GETDATE()");
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			return sql.replace("{C_GUID}", "SYS_GUID()").replace("'{C_NOW}'", "SYSDATE");
		}
		return sql;
	}
}
