package com.wisdge.ezcell;

import java.util.List;

public class EzPreview {
	private int totalRows;
	private int totalCols;
	private List<Object> elements;

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalCols() {
		return totalCols;
	}

	public void setTotalCols(int totalCols) {
		this.totalCols = totalCols;
	}

	public List<Object> getElements() {
		return elements;
	}

	public void setElements(List<Object> elements) {
		this.elements = elements;
	}
	
	public EzPreview(int rows, int cols, List<Object> elements) {
		this.totalRows = rows;
		this.totalCols = cols;
		this.elements = elements;
	}

}
