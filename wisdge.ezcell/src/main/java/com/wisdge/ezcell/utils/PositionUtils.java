package com.wisdge.ezcell.utils;

public class PositionUtils {

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
