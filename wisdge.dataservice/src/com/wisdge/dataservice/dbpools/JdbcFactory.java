package com.wisdge.dataservice.dbpools;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.wisdge.utils.DomUtils;
import com.wisdge.utils.StringUtils;

public class JdbcFactory {
	private static final Log logger = LogFactory.getLog(JdbcFactory.class);
	
	private String configXML;
	private Map<String, JdbcEntry> jdbcEntries;

	public String getConfigXML() {
		return configXML;
	}

	public void setConfigXML(String configXML) {
		this.configXML = configXML;
	}
	
	public void init() throws Exception {
		ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
		try {
			InputStream in = resourceLoader.getResource(configXML).getInputStream();
			try {
				Document doc = DomUtils.parser(in);
				// logger.debug(doc.asXML());
				Element root = doc.getRootElement();
				//System.out.println(root.asXML());
				
				jdbcEntries = new HashMap<String, JdbcEntry>();
				List<Element> els = root.elements("jdbc");
				for(Element el : els) {
					JdbcEntry entry = new JdbcEntry();
					String jndi = el.elementTextTrim("jndi");
					try {
						if (StringUtils.isEmpty(jndi)) {
							String driverClassName = el.elementTextTrim("driverClassName");
							String url = el.elementTextTrim("url");
							String username = el.elementTextTrim("username");
							String password = el.elementTextTrim("password");
							
							BasicDataSource dataSource = new BasicDataSource();
							dataSource.setDriverClassName(driverClassName);
							dataSource.setUrl(url);
							dataSource.setUsername(username);
							dataSource.setPassword(password);
							entry.setDataSource(dataSource);
							logger.debug("Create datasource: [" + driverClassName + "][" + url + "][" + username + "][***]");
						} else {
							Context ctx = new InitialContext();
							entry.setDataSource((DataSource)ctx.lookup(jndi));
							entry.setJndi(true);
							logger.debug("Get JNDI datasource: [" + jndi + "]");
						}
						
						DataSourceTransactionManager tm = new DataSourceTransactionManager(entry.getDataSource());
						JdbcTemplate jdbcTemplate = new JdbcTemplate(entry.getDataSource());
						entry.setTransactionManager(tm);
						entry.setJdbcTemplate(jdbcTemplate);
						entry.setDbType(el.attributeValue("type"));
						jdbcEntries.put(el.attributeValue("name"), entry);
					} catch(Exception e) {
						logger.error(e, e);
					}
				}
			} catch(Exception e) {
				logger.error(e, e);
			} finally {
				in.close();
			}
		} catch(Exception e) {
			logger.error(e, e);
		}
	}
	
	public JdbcEntry getEntry(String name) {
		return jdbcEntries.get(name);
	}
	
	public boolean hasEntry(String name) {
		return jdbcEntries.containsKey(name);
	}
	
	public void destroy() {
		Iterator<String> iter = jdbcEntries.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			try {
				DataSource dataSource = jdbcEntries.get(key).getDataSource();
				if (dataSource instanceof BasicDataSource)
					((BasicDataSource) dataSource).close();
				else
					dataSource.getConnection().close();
			} catch (SQLException e) {
				logger.error(e, e);
			}
		}
		jdbcEntries.clear();
	}
}
