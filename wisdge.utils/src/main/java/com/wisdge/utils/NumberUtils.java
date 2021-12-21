package com.wisdge.utils;

import java.text.NumberFormat;

/**
 * Description: Number utility classes
 * 
 * @author Kevin MOU
 * @version 1.1
 */
public class NumberUtils {
	private static NumberFormat nf = NumberFormat.getNumberInstance();
	private static NumberFormat cur = NumberFormat.getCurrencyInstance();
	public static final double doubleExp = 10E-10;
	public static final double floatExp = 10E-5;

	/**
	 * 将浮点型数字变更为货币性字符串
	 * 
	 * @param num
	 *            浮点数字
	 * @return String 表示货币的字符串
	 */
	public static String Number2Currency(double num) {
		return nf.format(num);
	}

	/**
	 * 将长整型数字变更为货币性字符串
	 * 
	 * @param num
	 *            长整型数字
	 * @return String 表示货币的字符串
	 */
	public static String Number2Currency(long num) {
		return nf.format(num);
	}

	/**
	 * 将浮点型数字变更为带符号的货币性字符串
	 * 
	 * @param num
	 *            浮点型数字
	 * @return String 表示货币的字符串
	 */
	public static String FormatCurrentcy(double num) {
		return cur.format(num);
	}

	/**
	 * 对浮点型数字的小数点后的长度进行截除
	 * 
	 * @param val
	 *            浮点型数字
	 * @param n
	 *            小数点末尾长度
	 * @return float 格式后的浮点型数字
	 */
	public static float tofNPrecision(float val, int n) {
		double temp = Math.pow(10, n);
		long l = (long) (val * temp);
		return (float) (l / temp);
	}

	/**
	 * 对double型数字的小数点后的长度进行截除
	 * 
	 * @param val
	 *            double型数字
	 * @param n
	 *            小数点末尾长度
	 * @return double 格式后的double型数字
	 */
	public static double todNPrecision(double val, int n) {
		double temp = Math.pow(10, n);
		long l = (long) (val * temp);
		return l / temp;
	}

	/**
	 * 非零判断，用于判断近似零的double数值
	 * 
	 * @param value
	 *            double型数字
	 * @return boolean 当数字近似于零时返回 true
	 */
	public static boolean isZero(double d) {
		return Math.abs(d) > -1 * doubleExp && Math.abs(d) < doubleExp;
	}

	/**
	 * 非零判断，用于判断近似零的浮点型数值
	 * 
	 * @param value
	 *            浮点型数字
	 * @return boolean 当数字近似于零时返回 true
	 */
	public static boolean isZero(float f) {
		return Math.abs(f) > -1 * floatExp && Math.abs(f) < floatExp;
	}

	/**
	 * 比较两个double数字的近似值
	 * 
	 * @param first
	 *            源数字
	 * @param second
	 *            目标数字
	 * @return boolean 当两个double型数字近似时返回 true
	 */
	public static boolean equals(double first, double second) {
		return isZero(first - second);
	}

	public static int compare(double d1, double d2) {
		if (d1 - d2 > doubleExp)
			return 1;
		else if (d1 - d2 < -1 * doubleExp)
			return -1;
		else
			return 0;
	}

	/**
	 * 比较两个浮点型数字的近似值
	 * 
	 * @param first
	 *            源数字
	 * @param second
	 *            目标数字
	 * @return boolean 当两个浮点型数字近似时返回 true
	 */
	public static boolean equals(float first, float second) {
		return isZero(first - second);
	}

	/*
	 * 基本数字单位;
	 */
	private static final String[] units = { "千", "百", "十", "" };// 个位

	/*
	 * 大数字单位;
	 */
	private static final String[] bigUnits = { "万", "亿" };

	/*
	 * 中文数字;
	 */
	private static final char[] numChars = { '一', '二', '三', '四', '五', '六', '七', '八', '九' };

	// private static final char[] numMouneyChars =
	// { '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };
	private static char numZero = '零';

	/**
	 * 将中文数字转换为阿拉伯数字;
	 * 
	 * @param numberCN
	 *            中文大写数字
	 * @return 阿拉伯数字
	 * @throws Exception
	 *             转换异常
	 */
	public static int cn2Arab(String numberCN) throws Exception {
		String tempNumberCN = numberCN;
		// 异常数据处理;
		if (tempNumberCN == null) {
			return 0;
		}

		/*
		 * nums[0] 保存以千单位; nums[1] 保存以万单位; nums[2] 保存以亿单位;
		 */
		String[] nums = new String[bigUnits.length + 1];

		// 千位以内,直接处理;
		nums[0] = tempNumberCN;

		/*
		 * 分割大数字,以千为单位进行运算;
		 */
		for (int i = (bigUnits.length - 1); i >= 0; i--) {

			// 是否存在大单位(万,亿...);
			int find = tempNumberCN.indexOf(bigUnits[i]);

			if (find != -1) {
				String[] tempStrs = tempNumberCN.split(bigUnits[i]);

				// 清空千位内容;
				if (nums[0] != null) {
					nums[0] = null;
				}

				if (tempStrs[0] != null) {
					nums[i + 1] = tempStrs[0];
				}

				if (tempStrs.length > 1) {
					tempNumberCN = tempStrs[1];

					if (i == 0) {
						nums[0] = tempStrs[1];
					}

				} else {
					tempNumberCN = null;
					break;
				}
			}
		}

		String tempResultNum = "";
		for (int i = nums.length - 1; i >= 0; i--) {
			if (nums[i] != null) {
				tempResultNum += cn2Arab_K(nums[i]);
			} else {
				tempResultNum += "0000";
			}
		}

		return Integer.parseInt(tempResultNum);
	}

	/**
	 * 将一位中文数字转换为一位数字; <br>
	 * eg: 一 返回 1;
	 * 
	 * @param onlyCNNumber
	 *            中文单字
	 * @return 整型数字单数
	 * @throws Exception
	 *             转换异常
	 */
	public static int cn2Arab(char onlyCNNumber) throws Exception {
		if (numChars[0] == onlyCNNumber) {
			return 1;
		} else if (numChars[1] == onlyCNNumber || onlyCNNumber == '两') {// 处理中文习惯用法(二,两)
			return 2;
		} else if (numChars[2] == onlyCNNumber) {
			return 3;
		} else if (numChars[3] == onlyCNNumber) {
			return 4;
		} else if (numChars[4] == onlyCNNumber) {
			return 5;
		} else if (numChars[5] == onlyCNNumber) {
			return 6;
		} else if (numChars[6] == onlyCNNumber) {
			return 7;
		} else if (numChars[7] == onlyCNNumber) {
			return 8;
		} else if (numChars[8] == onlyCNNumber) {
			return 9;
		}

		throw new NumberFormatException("Chat " + onlyCNNumber + " is not number format");
	}

	/**
	 * 将一位数字转换为一位中文数字; eg: 1 返回 一;
	 * 
	 * @param onlyArabNumber
	 *            数字单数
	 * @return 中文单字
	 */
	public static char arab2Cn(char onlyArabNumber) {
		if (onlyArabNumber == '0') {
			return numZero;
		}

		if (onlyArabNumber > '0' && onlyArabNumber <= '9') {
			return numChars[onlyArabNumber - '0' - 1];
		}

		return onlyArabNumber;
	}

	/**
	 * 转换整型数字到中文大写
	 * 
	 * @param num
	 *            整型数字
	 * @return 中文大写
	 */
	public static String arab2Cn(Integer num) {
		String tempNum = num + "";

		// 传说中的分页算法;
		int numLen = tempNum.length();
		int start = 0;
		int end = 0;
		int per = 4;
		int total = (int) ((numLen + per - 1) / per);
		int inc = numLen % per;

		/*
		 * 123,1234,1234 四位一段,进行处理;
		 */
		String[] numStrs = new String[total];
		for (int i = total - 1; i >= 0; i--) {
			start = (i - 1) * per + inc;

			if (start < 0) {
				start = 0;
			}

			end = i * per + inc;

			numStrs[i] = tempNum.substring(start, end);
		}

		String tempResultNum = "";
		int rempNumsLen = numStrs.length;
		for (int i = 0; i < rempNumsLen; i++) {

			// 小于1000补零处理;
			if (i > 0 && Integer.parseInt(numStrs[i]) < 1000) {
				tempResultNum += numZero + arab2Cn_K(Integer.parseInt(numStrs[i]));
			} else {
				tempResultNum += arab2Cn_K(Integer.parseInt(numStrs[i]));
			}

			// 加上单位(万,亿....)
			if (i < rempNumsLen - 1) {
				tempResultNum += bigUnits[rempNumsLen - i - 2];
			}

		}

		// 去掉未位的零
		tempResultNum = tempResultNum.replaceAll(numZero + "$", "");
		return tempResultNum;
	}

	/**
	 * 将千以内的数字转换为中文数字;
	 * 
	 * @param num
	 *            整型数字
	 * @return 中文大写
	 */
	private static String arab2Cn_K(Integer num) {

		char[] numChars = (num + "").toCharArray();

		String tempStr = "";

		int inc = units.length - numChars.length;

		for (int i = 0; i < numChars.length; i++) {
			if (numChars[i] != '0') {
				tempStr += arab2Cn(numChars[i]) + units[i + inc];
			} else {
				tempStr += arab2Cn(numChars[i]);
			}
		}

		// 将连续的零保留一个
		tempStr = tempStr.replaceAll(numZero + "+", numZero + "");

		// 去掉未位的零
		tempStr = tempStr.replaceAll(numZero + "$", "");

		return tempStr;

	}

	/**
	 * 处理千以内中文数字,返回4位数字字符串,位数不够以"0"补齐;
	 * 
	 * @param numberCN
	 * @return
	 * @throws Exception
	 */
	private static String cn2Arab_K(String numberCN) throws Exception {

		if ("".equals(numberCN)) {
			return "";
		}

		int[] nums = new int[4];
		if (numberCN != null) {
			for (int i = 0; i < units.length; i++) {
				int idx = numberCN.indexOf(units[i]);
				if (idx > 0) {
					char tempNumChar = numberCN.charAt(idx - 1);
					int tempNumInt = cn2Arab(tempNumChar);
					nums[i] = tempNumInt;
				}
			}

			// 处理十位
			char ones = numberCN.charAt(numberCN.length() - 1);
			nums[nums.length - 1] = cn2Arab(ones);

			// 处理个位
			if ((numberCN.length() == 2 || numberCN.length() == 1) && numberCN.charAt(0) == '十') {
				nums[nums.length - 2] = 1;
			}
		}

		// 返回结果
		String tempNum = "";
		for (int i = 0; i < nums.length; i++) {
			tempNum += nums[i];
		}

		return (tempNum);
	}

	private static char[] cns = { '一', '二', '三', '四', '五', '六', '七', '八', '九', '千', '百', '十', '万', '亿' };

	private static boolean containArabs(char c) {
		for (int i = 0; i < cns.length; i++)
			if (cns[i] == c)
				return true;
		return false;
	}

	public static String makeCn2Arab(String cn) {
		for (int i = 0; i < cn.length(); i++) {
			char c = cn.charAt(i);
			if (!containArabs(c))
				return cn;
		}
		try {
			return Integer.toString(cn2Arab(cn));
		} catch (Exception e) {
			e.printStackTrace();
			return cn;
		}
	}
}
