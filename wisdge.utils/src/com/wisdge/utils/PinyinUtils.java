package com.wisdge.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Description: ZH_CN translate to full spell
 * 
 * @author Kevin MOU
 * @version 1.2
 */
public class PinyinUtils {

	/**
	 * 返回字符串的拼音的首字母缩写
	 * 
	 * @param source
	 *            String 字符串
	 * @return String 转换成全拼后的首字母字符串
	 */
	public static String getPinyin(String source) {
		return getPinyin(source, "");
	}

	public static String getPinyin(String source, String spliter) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		String abbr = "";
		for (char c : source.toCharArray()) {
			if (!isChinese(c) && c != '〇') {
				abbr += c;
			} else {
				try {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
					abbr += getFirstString(pys);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					abbr += c;
				}
			}
			abbr += spliter;
		}
		return abbr;
	}

	private static String getFirstString(String[] stringArray) {
		if (stringArray != null && stringArray.length > 0) {
			return stringArray[0];
		}
		return "";
	}

	/**
	 * 返回字符串的拼音的首字母缩写
	 * 
	 * @param source
	 *            String 字符串
	 * @return String 转换成全拼后的首字母字符串
	 */
	public static String getPinyinAbbr(String source) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		String abbr = "";
		for (char c : source.toCharArray()) {
			if (!isChinese(c) && c != '〇') {
				abbr += c;
			} else {
				try {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
					abbr += getFirstStringAbbr(pys);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					abbr += c;
				}
			}
		}
		return abbr;
	}

	private static char getFirstStringAbbr(String[] stringArray) {
		if (stringArray != null && stringArray.length > 0) {
			String firstString = stringArray[0];
			if (firstString.length() > 0)
				return firstString.charAt(0);
			return 0;
		}
		return 0;
	}

	/**
	 * 判断字节是否为中文
	 * 
	 * @param c
	 *            字节
	 * @return boolean true=中文
	 */
	public static boolean isChinese(char c) {
		String regex = "[\\u4e00-\\u9fa5]";
		return String.valueOf(c).matches(regex);
	}
}
