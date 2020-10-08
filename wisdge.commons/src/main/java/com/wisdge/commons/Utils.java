package com.wisdge.commons;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Utils {
	
	/**
	 * 连续在多个字符串中按顺序选择一个非空的字符串返回
	 * @param strings String[] 备选的字符串
	 * @return String 首选的非空字符串
	 */
	public static String untilNotEmpty(String...strings) {
		for(String string:strings) {
			if (! StringUtils.isEmpty(string))
				return string;
		}
		return "";
	}
	
	public static String i18n(String baseName, Locale locale, String key, Object...objects) throws Exception {
		if (locale == null)
			locale = Locale.getDefault();
		
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/" + baseName, locale);
		if (bundle.containsKey(key)) {
			String resource = bundle.getString(key);
			if (objects != null && objects.length > 0)
				return MessageFormat.format(resource, objects);
			return resource;
		} else {
			return key;
		}
	}

	/**
	 * 判断文件是否包含在路径描述中（适用通配符）
	 * @param pathRegex String 路径通配符描述
	 * @param file String 文件名（包含路径）
	 * @return boolean
	 */
	public static boolean pathMatches(String pathRegex, String file) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pathRegex);
		Path path = Paths.get(file);
		return matcher.matches(path);
	}

	@Test
	public void test() {
		String ignore = "js/app/**";
		String name = "js/app/ios.js";
		String name2 = "js/app/ios/a/b/c.js";

		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + ignore);
		System.out.println(matcher.matches(Paths.get(name)));
		System.out.println(matcher.matches(Paths.get(name2)));
	}

}
