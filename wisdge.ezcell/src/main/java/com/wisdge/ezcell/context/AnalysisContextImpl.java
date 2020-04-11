package com.wisdge.ezcell.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wisdge.ezcell.annotation.ExcelTypeEnum;
import com.wisdge.ezcell.event.AnalysisEventListener;
import com.wisdge.ezcell.exception.ExcelAnalysisException;
import com.wisdge.ezcell.meta.BaseRowModel;
import com.wisdge.ezcell.meta.ExcelHeadProperty;
import com.wisdge.ezcell.meta.Sheet;

public class AnalysisContextImpl implements AnalysisContext {
	private Map<String, Object> custom;
	private Sheet currentSheet;
	private ExcelTypeEnum excelType;
	private InputStream inputStream;
	private AnalysisEventListener<?> eventListener;
	private Integer currentRowNo;
	private Integer totalRows;
	private Integer totalCols;
	private ExcelHeadProperty excelHeadProperty;
	private boolean use1904WindowDate = false;

	@Override
	public void setUse1904WindowDate(boolean use1904WindowDate) {
		this.use1904WindowDate = use1904WindowDate;
	}

	@Override
	public Object getCurrentRowAnalysisResult() {
		return currentRowAnalysisResult;
	}

	@Override
	public void interrupt() {
		throw new ExcelAnalysisException("interrupt error");
	}

	@Override
	public boolean use1904WindowDate() {
		return use1904WindowDate;
	}

	@Override
	public void setCurrentRowAnalysisResult(Object currentRowAnalysisResult) {
		this.currentRowAnalysisResult = currentRowAnalysisResult;
	}

	private Object currentRowAnalysisResult;

	public AnalysisContextImpl(InputStream inputStream, ExcelTypeEnum excelTypeEnum, Map<String, Object> custom, AnalysisEventListener<?> listener) {
		this.custom = custom;
		this.eventListener = listener;
		this.inputStream = inputStream;
		this.excelType = excelTypeEnum;
	}

	@Override
	public void setCurrentSheet(Sheet currentSheet) {
		cleanCurrentSheet();
		this.currentSheet = currentSheet;
		if (currentSheet.getClazz() != null) {
			buildExcelHeadProperty(currentSheet.getClazz(), null);
		}
	}

	private void cleanCurrentSheet() {
		this.currentSheet = null;
		this.excelHeadProperty = null;
		this.totalRows = 0;
		this.totalCols = 0;
		this.currentRowAnalysisResult = null;
		this.currentRowNo = 0;
	}

	@Override
	public ExcelTypeEnum getExcelType() {
		return excelType;
	}

	public void setExcelType(ExcelTypeEnum excelType) {
		this.excelType = excelType;
	}

	public Map<String, Object> getCustom() {
		return custom;
	}

	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}

	@Override
	public Sheet getCurrentSheet() {
		return currentSheet;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public AnalysisEventListener<?> getEventListener() {
		return eventListener;
	}

	public void setEventListener(AnalysisEventListener<?> eventListener) {
		this.eventListener = eventListener;
	}

	@Override
	public Integer getCurrentRowNo() {
		return this.currentRowNo;
	}

	@Override
	public void setCurrentRowNo(Integer row) {
		this.currentRowNo = row;
	}

	@Override
	public Integer getTotalRows() {
		return totalRows;
	}

	@Override
	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	@Override
	public Integer getTotalCols() {
		return totalCols;
	}

	@Override
	public void setTotalCols(Integer totalCols) {
		this.totalCols = totalCols;
	}

	@Override
	public ExcelHeadProperty getExcelHeadProperty() {
		return this.excelHeadProperty;
	}

	@Override
	public void buildExcelHeadProperty(Class<? extends BaseRowModel> clazz, List<String> headOneRow) {
		if (this.excelHeadProperty == null && (clazz != null || headOneRow != null)) {
			this.excelHeadProperty = new ExcelHeadProperty(clazz, new ArrayList<List<String>>());
		}
		if (this.excelHeadProperty.getHead() == null && headOneRow != null) {
			this.excelHeadProperty.appendOneRow(headOneRow);
		}
	}
}
