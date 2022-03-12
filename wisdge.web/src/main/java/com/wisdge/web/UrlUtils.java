package com.wisdge.web;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.*;
import com.wisdge.utils.StringUtils;

/**
 * Description: Web Content Management Copyright: (c) 2003
 *
 * @author Kevin MOU
 * @version 1.0
 */
public class UrlUtils {
	// Default locale to use for serving requests to help
	private static String defaultLocale = null;

	/**
	 * 判断是否本地request请求
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return boolean 判断结果
	 */
	public static boolean isLocalhost(HttpServletRequest request) {
		String reqIP = request.getRemoteAddr();
		if ("127.0.0.1".equals(reqIP) || "localhost".equals(reqIP)) {
			return true;
		}
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			InetAddress[] addr = InetAddress.getAllByName(hostname);
			for (int i = 0; i < addr.length; i++) {
				// test all addresses retrieved from the local machine
				if (addr[i].getHostAddress().equals(reqIP))
					return true;
			}
		} catch (IOException ioe) {
		}
		return false;
	}

	/**
	 * 获得本地化语言对象
	 *
	 * @param request
	 *            HttpServletRequest请求对象
	 * @param response
	 *            HttpServletResponse相应对象 HttpServletResponse or null (locale will not be persisted in session cookie)
	 * @return Locale对象
	 */
	public static Locale getLocaleObj(HttpServletRequest request, HttpServletResponse response) {
		String localeStr = getLocale(request, response);
		if (localeStr.length() >= 5) {
			return new Locale(localeStr.substring(0, 2), localeStr.substring(3, 5));
		} else if (localeStr.length() >= 2) {
			return new Locale(localeStr.substring(0, 2), "");
		} else {
			return Locale.getDefault();
		}
	}

	/**
	 * 获得本地语言设置
	 *
	 * @param request
	 *            HttpServletRequest请求对象
	 * @param response
	 *            HttpServletResponse相应对象 HttpServletResponse or null (locale will not be persisted in session cookie)
	 * @return String 本地化语言
	 */
	public static String getLocale(HttpServletRequest request, HttpServletResponse response) {
		if (defaultLocale == null) {
			initializeLocales();
		}
		if (request == null) {
			return defaultLocale;
		}
		// use locale passed in a request in current user session
		String forcedLocale = getForcedLocale(request, response);
		if (forcedLocale != null) {
			return forcedLocale;
		}
		return request.getLocale().toString();
	}

	/**
	 * Obtains locale passed as lang parameter with a request during user session
	 *
	 * @param request
	 * @param response
	 *            response or null; if null, locale will not be persisted (in session cookie)
	 * @return ll_CC or ll or null
	 */
	private static String getForcedLocale(HttpServletRequest request, HttpServletResponse response) {
		// get locale passed in this request
		String forcedLocale = request.getParameter("lang");
		if (forcedLocale != null) {
			// save locale (in session cookie) for later use in a user session
			if (response != null) {
				Cookie cookieTest = new Cookie("lang", forcedLocale);
				response.addCookie(cookieTest);
			}
		} else {
			// check if locale was passed earlier in this session
			Cookie[] cookies = request.getCookies();
			for (int c = 0; cookies != null && c < cookies.length; c++) {
				if ("lang".equals(cookies[c].getName())) {
					forcedLocale = cookies[c].getValue();
					break;
				}
			}
		}
		// format forced locale
		if (forcedLocale != null) {
			if (forcedLocale.length() >= 5) {
				forcedLocale = forcedLocale.substring(0, 2) + "_" + forcedLocale.substring(3, 5);
			} else if (forcedLocale.length() >= 2) {
				forcedLocale = forcedLocale.substring(0, 2);
			}
		}
		return forcedLocale;
	}

	/**
	 * If locales for infocenter specified in prefernces or as command line parameters, this methods stores these locales in locales local variable for later
	 * access.
	 */
	private static synchronized void initializeLocales() {
		if (defaultLocale != null) {
			// already initialized
			return;
		}
		// initialize default locale
		defaultLocale = Locale.getDefault().toString();
	}

	public static String getBaseUrl(URL url) {
		String baseUrl = url.getProtocol() + "://" + url.getHost();
		int port = url.getPort();
		if (port != -1)
			baseUrl += ":" + url.getPort();
		return baseUrl;
	}

	/**
	 * 取得URL的上一级路径
	 *
	 * @param url
	 *            目标URL
	 * @return String 父路径
	 */
	public static String getParentUrl(URL url) {
		String protocal = url.getProtocol();
		String host = url.getHost();
		int port = url.getPort();
		String path = url.getPath();
		// System.out.println(path);
		int i = path.lastIndexOf("/");
		if (i == -1)
			return "";
		String webinf = "";
		if (host.length() > 0)
			webinf += "//" + host;
		if (port != -1)
			webinf += ":" + port;
		return protocal + ":" + webinf + path.substring(0, i + 1);
	}

	/**
	 * 对URL 进行UTF-8字符码解析 本方法为快速简易实现提供帮助
	 *
	 * @param url
	 *            被解析的字符串
	 * @param encoding
	 *            编码类型
	 * @return 解析后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String url, String encoding) throws UnsupportedEncodingException {
		return URLDecoder.decode(url, encoding);
	}
	public static String decode(String url) throws UnsupportedEncodingException {
		return decode(url, "UTF-8");
	}

	/**
	 * 对字符串进行URL编码，并对URL中的路径符号 "/"不进行编码
	 *
	 * @param url
	 *            被编码的字符串
	 * @param encoding
	 *            编码类型
	 * @return 编码后的字符串
	 */
	public static String encode(String url, String encoding) throws UnsupportedEncodingException {
		StringBuffer buf = new StringBuffer();
		String[] tokens = url.split("/");
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() == 0)
				continue;
			if (i > 0)
				buf.append("/");
			buf.append(URLEncoder.encode(tokens[i], encoding));
		}
		return buf.toString();
	}
	public static String encode(String url) throws UnsupportedEncodingException {
		return encode(url, "UTF-8");
	}

	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (j == '/')
				tmp.append(j);
			else {
				if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
					tmp.append(j);
				else if (j < 256) {
					tmp.append("%");
					if (j < 16)
						tmp.append("0");
					tmp.append(Integer.toString(j, 16));
				} else {
					tmp.append("%u");
					tmp.append(Integer.toString(j, 16));
				}
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	public static String concat(String ...strings) {
		StringBuilder builder = new StringBuilder();
		boolean first = true, e = false;
		for(String str : strings) {
			if (StringUtils.isEmpty(str))
				continue;

			if (first)
				builder.append(str);
			else {
				if (str.startsWith("/"))
					builder.append(e?str.substring(1):str);
				else
					builder.append(e?str:("/" + str));
			}
			e = str.endsWith("/");
			first = false;
		}
		return builder.toString();
	}

	/**
	 * 判断url是否符合domain规则
	 * @param url String
	 * @param domain String
	 * @return
	 */
	public static boolean isMatchDomain(String url, String domain) {
		Pattern urlPattern = Pattern.compile("^http(s?)://" + domain.trim().toLowerCase(Locale.ROOT).replace(".", "\\.") + "/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		Matcher matcher = urlPattern.matcher(url);
		return matcher.find();
	}

	/**
	 * 判断url是否符合domain规则
	 * @param url String
	 * @param domains Set<String>
	 * @return
	 */
	public static boolean isMatchDomains(String url, Set<String> domains) {
		for(String domain : domains) {
			Pattern urlPattern = Pattern.compile("^http(s?)://" + domain.trim().toLowerCase(Locale.ROOT).replace(".", "\\.") + "/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher matcher = urlPattern.matcher(url);
			return matcher.find();
		}
		return false;
	}

	/**
     *  Parses a query string into a parameter map, decoding values as needed.
     *  Does not support multiple parameter values; later values will overwrite
     *  earlier.
     *
	 *  @param  query       The query string: zero or more <code>name=value</code>
	 *                      pairs separated by ampersands, with or without a
     *                      leading question mark.
     *  @param  ignoreEmpty If <code>true</code>, ignores any entries without a
     *                      value (eg, "<code>name=</code>"; if <code>false</code>
	 *                      these are added to the map with an empty string for
	 *                      the value.
	 *  @return A map of the name-value pairs. Caller is permitted to modify this
	 *          map.
	 *  @throws RuntimeException on any failure.
	 */
	public static Map<String, String> parseQueryString(String query, boolean ignoreEmpty) throws UnsupportedEncodingException {
		Map<String, String> result = new HashMap<>();
		if ((query == null) || (query.length() == 0))
			return result;

		if (query.charAt(0) == '?')
			query = query.substring(1);
		// need to repeat test for empty string
		if (query.length() == 0)
			return result;

		for (String param : query.split("&")) {
			// why not use split again? because it doesn't handle a missing '='
			int delimIdx = param.indexOf('=');
			if (delimIdx < 0)
				throw new RuntimeException("Not parsable parameter: " + param);

			String name = param.substring(0, delimIdx);
			String value = param.substring(delimIdx + 1);

			if ((value.length() > 0) || !ignoreEmpty)
				result.put(name, decode(value));
		}

		return result;
	}
}
