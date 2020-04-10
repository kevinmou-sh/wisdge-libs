package com.wisdge.web.filters;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StringUtils;

/**
 * Filters Http requests and removes malicious characters/strings
 * (i.e. XSS) from the Query String
 */
public class XSSPreventionFilter implements Filter {
	private static final String[] filterLabels = {"applet", "blink", "frameset", "iframe", "object", 
			"base", "body", "head", "layer", "style", "basefont", "embed", "html", "link", 
			"title", "bgsound", "frame", "ilayer", "meta", "script"};
	private static final String[] filterAttributes = {"dynsrc", "action", "background", "bgsound", "lowsrc",
			"value", "onmouse(.*?)"}; // src和href暂时不过滤
	private static final String[] filterKeywords = {"vbscript:", "ms-its:", "firefoxurl:", "javascript:", 
			"mhtml:", "mocha:", "data:", "livescript:"};
	
	private FilterConfig filterConfig;
	
	class XSSRequestWrapper extends HttpServletRequestWrapper {

		private String allowDomains;
		private Map<String, String[]> sanitizedQueryString;
		
		public XSSRequestWrapper(HttpServletRequest request, String allowDomains) {
			super(request);
			this.allowDomains = allowDomains;
		}
		
		//QueryString overrides
		
		@Override
		public String getParameter(String name) {
			String parameter = null;
			String[] vals = getParameterMap().get(name); 
			
			if (vals != null && vals.length > 0) {
				parameter = vals[0];
			}
			return parameter;
		}

		@Override
		public String[] getParameterValues(String name) {
			return getParameterMap().get(name);
		}
		
		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(getParameterMap().keySet());
		}

		@Override
		public Map<String,String[]> getParameterMap() {
			if(sanitizedQueryString == null) {
				Map<String, String[]> res = new HashMap<String, String[]>();
				Map<String, String[]> originalQueryString = super.getParameterMap();
				if(originalQueryString!=null) {
					for (String key : (Set<String>) originalQueryString.keySet()) {
						String[] rawVals = originalQueryString.get(key);
						String[] snzVals = new String[rawVals.length];
						for (int i=0; i < rawVals.length; i++) {
							snzVals[i] = stripXSS(rawVals[i]);
							//System.out.println("Sanitized: " + rawVals[i] + " to " + snzVals[i]);
						}
						res.put(stripXSS(key), snzVals);
					}
				}
				sanitizedQueryString = res;
			}
			return sanitizedQueryString;
		}

		/**
		 * Removes all the potentially malicious characters from a string
		 * @param value the raw string
		 * @return the sanitized string
		 */
		private String stripXSS(String value) {
			String cleanValue = null;
			if (value != null) {
				cleanValue = filter(value);
				
				// 如果配置了允许的域，则过滤src属性中的其他域
				if(allowDomains != null && !allowDomains.equals("")){
					String[] allDomainsArr = allowDomains.toLowerCase().split(",");
					Pattern scriptPattern = Pattern.compile("src[\r\n\\s]*=[\r\n\\s]*(\\\"|'|\\\\\")(.*?)(\\\"|'|\\\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
					Matcher srcMatcher = scriptPattern.matcher(cleanValue);
					while(srcMatcher.find()){
						int gc = srcMatcher.groupCount();
						if(gc >= 1){
							String gp = srcMatcher.group(2);
							boolean findAllowDomain = false;
							if(gp != null){
								gp = gp.toLowerCase();
								if(!gp.startsWith("http://") && !gp.startsWith("https://")){
									findAllowDomain = true;
								}else{
									for(String allowDomain : allDomainsArr){//查找每个允许的domain
										if(gp.startsWith(allowDomain)){
											 findAllowDomain = true;
											 break;
										}
									}
								}
								if(!findAllowDomain){//未找到，就过滤掉
									scriptPattern = Pattern.compile("src[\r\n\\s]*=[\r\n\\s]*(\\\"|'|\\\\\")" + gp +  "(\\\"|'|\\\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
									cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");
								}
							}
						}
					}
				}
				//cleanValue = cleanValue.replaceAll("&", "&amp;");
				//cleanValue = cleanValue.replaceAll("<", "&lt;");
				//cleanValue = cleanValue.replaceAll(">", "&gt;");
				//cleanValue = cleanValue.replaceAll("\"", "&quot;");
			}
			return cleanValue;
		}
	}

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String allowDomains = filterConfig.getInitParameter("allowDomains");
		String allowPaths = filterConfig.getInitParameter("allowPaths");

		String servletPath = ((HttpServletRequest) request).getPathInfo();
		if (servletPath == null) {
			servletPath = ((HttpServletRequest) request).getServletPath();
		}
		//System.out.println(servletPath);
		if (! StringUtils.isEmpty(allowPaths)) {
			for(String allow : allowPaths.split(",")) {
				if (servletPath.startsWith(allow)) {
					chain.doFilter(request, response);
					return;
				}
			}
		}

		XSSRequestWrapper wrapper = new XSSRequestWrapper((HttpServletRequest)request, allowDomains);
		chain.doFilter(wrapper, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
	
	public static String filter(String value) {
		String cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD);

		// Avoid null characters
		cleanValue = cleanValue.replaceAll("\0", "").replaceAll("'", "");
		
		// Avoid anything between script tags
		Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
		cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");
 

		// Avoid eval(...) expressions
		scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
		
		// Avoid expression(...) expressions
		scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
		
		// Avoid onload= expressions
		scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");

		// Avoid alert() expressions
		scriptPattern = Pattern.compile("alert\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

		// 过滤所有标签
		for(String filterAttibute : filterAttributes){
			scriptPattern = Pattern.compile(filterAttibute + "[\r\n]*=[\r\n]*(\\\"|')(.*?)(\\\"|')", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
		}
		// 过滤属性
		for(String filterKeyword : filterKeywords){
			scriptPattern = Pattern.compile(filterKeyword, Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
		}
		// 过滤关键字
		for(String filterLabel : filterLabels){
			scriptPattern = Pattern.compile("<" + filterLabel + "(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
			scriptPattern = Pattern.compile("</" + filterLabel + ">", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll(" ");
		}
		
		return cleanValue;
	}
}
