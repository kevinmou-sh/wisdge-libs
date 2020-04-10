package com.wisdge.dataservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class SqlTemplateManager {
	private static final Log logger = LogFactory.getLog(SqlTemplateManager.class);
	private List<String> resources;
	private Map<String, String> sqlMap;
	private Map<String, List<String>> worksets = new HashMap<String, List<String>>();
	private String defaultDbType;

	public String getDefaultDbType() {
		return defaultDbType;
	}

	public void setDefaultDbType(String defaultDbType) {
		this.defaultDbType = defaultDbType;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public void initialize() {
		sqlMap = new HashMap<String, String>();
		if (resources == null) {
			logger.error("Cann't find sql template resources defined.");
			return;
		}

		SAXReader reader = new SAXReader();
		try {
			StringBuilder buffer = new StringBuilder();
			for(String resource : resources) {
				Document document = reader.read(ResourceUtils.getFile(resource));
				Element root = document.getRootElement();
				List<?> setList = root.elements();
				// read all workset elements
				for (int i = 0; i < setList.size(); i++) {
					Element setElement = (Element) setList.get(i);
					String setName = setElement.attributeValue("name");
					List<String> workset = new ArrayList<String>();
					if (buffer.length() > 0)
						buffer.append(", ");
					buffer.append(setName);
					List<?> sqlList = setElement.elements();
					for (int k = 0; k < sqlList.size(); k++) {
						Element sqlElement = (Element) sqlList.get(k);
						String sqlName = sqlElement.attributeValue("name");
						String dbType = sqlElement.attributeValue("type");
						if (! workset.contains(sqlName))
							workset.add(sqlName);
						if (StringUtils.isEmpty(dbType))
							sqlMap.put((setName + "." + sqlName).toLowerCase(), sqlElement.getText());
						else
							sqlMap.put((setName + "." + sqlName + "." + dbType).toLowerCase(), sqlElement.getText());
					}
					if (worksets.containsKey(setName)) {
						throw new DuplicateException("发现重复的SQL模板Workset， name=" + setName);
					}
					worksets.put(setName, workset);
				}
			}
			logger.info("Loaded workset: " + buffer.toString());
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public String getTemplate(String templateName) throws NullPointerException {
		return getTemplate(templateName, null);
	}

	public String getTemplate(String templateName, String dbType) throws NullPointerException {
		String sql = null;
		if (dbType != null)
			sql = sqlMap.get((templateName + "." + dbType).toLowerCase());
		if (sql == null) {
			if (! StringUtils.isEmpty(defaultDbType))
				sql = sqlMap.get((templateName + "." + defaultDbType).toLowerCase());
			if (sql == null)
				sql = sqlMap.get(templateName.toLowerCase());
		}
		
		if (sql == null)
			throw new NullPointerException("Cann't found sql[" + templateName + "], DB type[" + dbType + "/" + defaultDbType + "]");

		return filterSql(sql.trim());
	}

	private String filterSql(String sql) {
		String str = sql.trim();
		str = str.replace('\n', ' ');
		str = str.replace('\t', ' ');

		return str;
	}
	
	public List<String> getWorkset(String worksetName) {
		return worksets.get(worksetName);
	}

}
