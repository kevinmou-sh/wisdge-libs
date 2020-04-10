package com.wisdge.web;

/**
 * HTML格式字符串操作类
 * 
 * @author Kevin MOU
 * @version 1.2
 */
public class HtmlUtils extends org.springframework.web.util.HtmlUtils {

	/**
	 * 编译HTML字符串, 包括编译空格、回车：
	 * 
	 * @param str
	 *            目标字符串
	 * @return 编译后的字符串
	 */
	public static String htmlEscapeEx(String str) {
		if (str == null)
			return "";
		
		str = htmlEscape(str);
		str = str.replaceAll(" ", "&nbsp;");
		str = str.replaceAll("/\n", "<br/>\n");
		str = str.replaceAll(new String(new byte[] { 13 }), "<br/>");
		return str;
	}

}