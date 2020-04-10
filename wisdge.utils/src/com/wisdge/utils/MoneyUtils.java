package com.wisdge.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MoneyUtils {
	/** 大写数字 */
	private static final String[] NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	/** 整数部分的单位 */
	private static final String[] IUNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
	/** 小数部分的单位 */
	private static final String[] DUNIT = { "角", "分", "厘" };

	/**
	 * 转换双精度浮点型数字到中文大写字符串
	 * 
	 * @param amount
	 *            double
	 * @return String
	 */
	public static String toChinese(double amount) {
		return toChinese(Double.toString(amount));
	}

	/**
	 * 转换浮点型数字到中文大写字符串
	 * 
	 * @param amount
	 *            double
	 * @return String
	 */
	public static String toChinese(float amount) {
		return toChinese(Float.toString(amount));
	}

	/**
	 * 转换长整型数字到中文大写字符串
	 * 
	 * @param amount
	 *            long
	 * @return String
	 */
	public static String toChinese(long amount) {
		return toChinese(Long.toString(amount));
	}

	/**
	 * 转换整型数字到中文大写字符串
	 * 
	 * @param amount
	 *            int
	 * @return String
	 */
	public static String toChinese(int amount) {
		return toChinese(Integer.toString(amount));
	}

	/**
	 * 转换大数值浮点数字到中文大写字符串
	 * 
	 * @param amount
	 *            BigDecimal
	 * @return String
	 */
	public static String toChinese(BigDecimal amount) {
		return toChinese(amount.toString());
	}

	/**
	 * 转大数值整数字到中文大写字符串
	 * 
	 * @param amount
	 *            BigInteger
	 * @return String
	 */
	public static String toChinese(BigInteger amount) {
		return toChinese(amount.toString());
	}

	/**
	 * 转换阿拉伯数字字符串为中文大写字符串
	 * 
	 * @param str
	 *            阿拉伯数字字符串
	 * @return 中文大写数字字符串
	 */
	public static String toChinese(String str) {
		str = str.replaceAll(",", "");// 去掉","
		String integerStr;// 整数部分数字
		String decimalStr;// 小数部分数字
		if (str.indexOf(".") > 0) {
			integerStr = str.substring(0, str.indexOf("."));
			decimalStr = str.substring(str.indexOf(".") + 1);
		} else if (str.indexOf(".") == 0) {
			integerStr = "";
			decimalStr = str.substring(1);
		} else {
			integerStr = str;
			decimalStr = "";
		}
		if (!integerStr.equals("")) {
			integerStr = Long.toString(Long.parseLong(integerStr));
			if (integerStr.equals("0")) {
				integerStr = "";
			}
		}
		if (integerStr.length() > IUNIT.length) {
			System.out.println(str + ":超出处理能力");
			return str;
		}

		int[] integers = toArray(integerStr);// 整数部分数字
		boolean isMust5 = isMust5(integerStr);// 设置万单位
		int[] decimals = toArray(decimalStr);// 小数部分数字
		return getChineseInteger(integers, isMust5) + getChineseDecimal(decimals);
	}

	private static int[] toArray(String number) {
		int[] array = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			array[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		return array;
	}

	private static String getChineseInteger(int[] integers, boolean isMust5) {
		StringBuffer chineseInteger = new StringBuffer("");
		int length = integers.length;
		for (int i = 0; i < length; i++) {
			String key = "";
			if (integers[i] == 0) {
				if ((length - i) == 13) {
					key = IUNIT[4];
				} else if ((length - i) == 9) {
					key = IUNIT[8];
				} else if ((length - i) == 5 && isMust5) {
					key = IUNIT[4];
				} else if ((length - i) == 1) {
					key = IUNIT[0];
				}
				if ((length - i) > 1 && integers[i + 1] != 0) {
					key += NUMBERS[0];
				}
			}
			chineseInteger.append(integers[i] == 0 ? key : (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
		}
		return chineseInteger.toString();
	}

	private static String getChineseDecimal(int[] decimals) {
		StringBuffer chineseDecimal = new StringBuffer("");
		for (int i = 0; i < decimals.length; i++) {
			if (i == 3) {
				break;
			}
			chineseDecimal.append(decimals[i] == 0 ? "" : (NUMBERS[decimals[i]] + DUNIT[i]));
		}
		return chineseDecimal.toString();
	}

	private static boolean isMust5(String integerStr) {
		int length = integerStr.length();
		if (length > 4) {
			String subInteger = "";
			if (length > 8) {
				subInteger = integerStr.substring(length - 8, length - 4);
			} else {
				subInteger = integerStr.substring(0, length - 4);
			}
			return Integer.parseInt(subInteger) > 0;
		} else {
			return false;
		}
	}
}