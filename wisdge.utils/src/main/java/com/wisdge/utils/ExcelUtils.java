package com.wisdge.utils;

import org.junit.Test;

public class ExcelUtils {

	/**
	 * 用于将Excel表格中列号字母转成列索引，从1对应A开始
	 * 
	 * @param value
	 *            列号
	 * @return 列索引
	 */
	public static int col2int(String value) {
		if (!value.matches("[A-Z]+")) {
			try {
				throw new Exception("Invalid parameter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int index = 0;
		char[] chars = value.toUpperCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			index += ((int) chars[i] - (int) 'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
		}
		return index;
	}

	/**
	 * 用于将excel表格中列索引转成列号字母，从A对应1开始
	 * 
	 * @param value
	 *            列索引
	 * @return 列号
	 */
	public static String int2col(int value) {
		if (value <= 0) {
			try {
				throw new Exception("Invalid parameter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		value--;
		String column = "";
		do {
			if (column.length() > 0) {
				value--;
			}
			column = ((char) (value % 26 + (int) 'A')) + column;
			value = (int) ((value - value % 26) / 26);
		} while (value > 0);
		return column;
	}

	@Test
	public void test() {
		int value = 26 * 26;
		String colStr = int2col(value);
		System.out.println(colStr);
		System.out.println(col2int(colStr));
	}
}
