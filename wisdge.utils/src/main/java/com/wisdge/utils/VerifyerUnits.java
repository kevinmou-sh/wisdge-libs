package com.wisdge.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证方法类
 * 
 * @author Kevin MOU
 * @version 1.0
 */
public class VerifyerUnits {
	/**
	 * 邮件格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+", 2);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * IP格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isIp(String str) {
		Pattern pattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 电话格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isPhone(String str) {
		Pattern pattern = Pattern.compile("(\\(\\d{3,4}\\)|\\d{3,4})\\s?(-|)?\\d{3,4}(-|)?\\d{4}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 移动电话格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isMobile(String str) {
		Pattern pattern = Pattern.compile("^1\\d{2}\\s?(-|)?\\d{4}\\s?(-|)?\\d{4}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 日期格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isDate(String str) {
		Pattern pattern = Pattern.compile("\\d{4}\\/?(-|)?\\d{1,2}\\/?(-|)?\\d{1,2}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 时间格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isTime(String str) {
		Pattern pattern = Pattern.compile("\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 邮编格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isPostCode(String str) {
		Pattern pattern = Pattern.compile("\\d{6}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 数字格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isDigit(String str) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 字符格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isAlphabetic(String str) {
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 数据格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isNumberic(String str) {
		Pattern pattern = Pattern.compile("(\\-|)\\d+(\\.\\d+|)");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * URL格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isUrl(String str) {
		Pattern pattern = Pattern.compile("(http:|https:|ftp:)//[^[A-Za-z0-9\\._\\?%&+\\-=/#]]*", 2);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 身份证格式校验
	 * 
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isIdentify(String str) {
		Pattern pattern = Pattern.compile("^(^\\d{15}|\\d{18}|\\d{17}(\\d|X|x))$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
