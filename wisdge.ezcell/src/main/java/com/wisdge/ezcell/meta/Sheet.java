package com.wisdge.ezcell.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sheet {
	private int headLineMun;
	/**
	 * Starting from 1
	 */
	private int sheetNo;
	private String sheetName;
	private Class<? extends BaseRowModel> clazz;
	private List<List<String>> head;
	private TableStyle tableStyle;
	/**
	 * column with
	 */
	private Map<Integer, Integer> columnWidthMap = new HashMap<Integer, Integer>();
	private Boolean autoWidth = Boolean.FALSE;
	private int startRow = 0;

	public Sheet(int sheetNo) {
		this.sheetNo = sheetNo;
	}

	public Sheet(int sheetNo, int headLineMun) {
		this.sheetNo = sheetNo;
		this.headLineMun = headLineMun;
	}

	public Sheet(int sheetNo, int headLineMun, Class<? extends BaseRowModel> clazz) {
		this.sheetNo = sheetNo;
		this.headLineMun = headLineMun;
		this.clazz = clazz;
	}

	public Sheet(int sheetNo, int headLineMun, Class<? extends BaseRowModel> clazz, String sheetName, List<List<String>> head) {
		this.sheetNo = sheetNo;
		this.clazz = clazz;
		this.headLineMun = headLineMun;
		this.sheetName = sheetName;
		this.head = head;
	}

	public List<List<String>> getHead() {
		return head;
	}

	public void setHead(List<List<String>> head) {
		this.head = head;
	}

	public Class<? extends BaseRowModel> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends BaseRowModel> clazz) {
		this.clazz = clazz;
		if (headLineMun == 0) {
			this.headLineMun = 1;
		}
	}

	public int getHeadLineMun() {
		return headLineMun;
	}

	public void setHeadLineMun(int headLineMun) {
		this.headLineMun = headLineMun;
	}

	public int getSheetNo() {
		return sheetNo;
	}

	public void setSheetNo(int sheetNo) {
		this.sheetNo = sheetNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public TableStyle getTableStyle() {
		return tableStyle;
	}

	public void setTableStyle(TableStyle tableStyle) {
		this.tableStyle = tableStyle;
	}

	public Map<Integer, Integer> getColumnWidthMap() {
		return columnWidthMap;
	}

	public void setColumnWidthMap(Map<Integer, Integer> columnWidthMap) {
		this.columnWidthMap = columnWidthMap;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder.append("Sheet{headLineMun=")
				.append(headLineMun)
				.append(", sheetNo=")
				.append(sheetNo)
				.append(", sheetName='")
				.append(sheetName)
				.append("', clazz=")
				.append(clazz)
				.append(", head=")
				.append(head)
				.append(", tableStyle=")
				.append(tableStyle)
				.append(", columnWidthMap=")
				.append(columnWidthMap)
				.append("}")
				.toString();
	}

	public Boolean getAutoWidth() {
		return autoWidth;
	}

	public void setAutoWidth(Boolean autoWidth) {
		this.autoWidth = autoWidth;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
}
