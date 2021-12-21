package com.wisdge.ezcell.utils;

public class PositionUtils {
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

	public static int getRow(String currentCellIndex) {
		int row = 0;
		if (currentCellIndex != null) {
			String rowStr = currentCellIndex.replaceAll("[A-Z]", "").replaceAll("[a-z]", "");
			row = Integer.parseInt(rowStr);
		}
		return row;
	}

	public static int getCol(String currentCellIndex) {
		int col = 0;
		if (currentCellIndex != null) {
			char[] currentIndex = currentCellIndex.replaceAll("[0-9]", "").toCharArray();
			for (int i = 0; i < currentIndex.length; i++) {
				col += (currentIndex[i] - '@') * Math.pow(26, (currentIndex.length - i - 1));
			}
		}
		return col;
	}
	

	/**
	 * Excel column index begin 1
	 * 
	 * @param colStr
	 * @return Integer
	 */
	public static int excelColStrToNum(String colStr) {
		int result = 0;
		int l = colStr.length();
		for (int i = 0; i < l; i++) {
			char ch = colStr.charAt(l - i - 1);
			result += ((int) (ch - 'A' + 1)) * Math.pow(26, i);
		}
		return result;
	}

	/**
	 * Excel column index begin 1
	 * 
	 * @param columnIndex
	 * @return
	 */
	public static String excelColIndexToStr(int columnIndex) {
		if (columnIndex <= 0) {
			return null;
		}
		String columnStr = "";
		columnIndex--;
		do {
			if (columnStr.length() > 0) {
				columnIndex--;
			}
			columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
			columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
		} while (columnIndex > 0);
		return columnStr;
	}

}
