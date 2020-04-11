package com.wisdge.ezcell.context;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.wisdge.ezcell.annotation.ExcelTypeEnum;
import com.wisdge.ezcell.event.AnalysisEventListener;
import com.wisdge.ezcell.meta.BaseRowModel;
import com.wisdge.ezcell.meta.ExcelHeadProperty;
import com.wisdge.ezcell.meta.Sheet;

/**
 *
 * A context is the main anchorage point of a excel reader.
 */
public interface AnalysisContext {
	/**
	 * Custom attribute
	 */
	Map<String, Object> getCustom();

	/**
	 * get current sheet
	 *
	 * @return current analysis sheet
	 */
	Sheet getCurrentSheet();

	/**
	 * set current sheet
	 * 
	 * @param sheet
	 */
	void setCurrentSheet(Sheet sheet);

	/**
	 *
	 * get excel type
	 * 
	 * @return excel type
	 */
	ExcelTypeEnum getExcelType();

	/**
	 * get inputStream
	 * 
	 * @return file inputStream
	 */
	InputStream getInputStream();

	/**
	 *
	 * custom listener
	 * 
	 * @return listener
	 */
	AnalysisEventListener<?> getEventListener();

	/**
	 * get current row
	 * 
	 * @return
	 */
	Integer getCurrentRowNo();

	/**
	 * set current row number
	 * 
	 * @param row
	 */
	void setCurrentRowNo(Integer row);

	/**
	 * get total rows
	 * 
	 * @return
	 */
	Integer getTotalRows();

	/**
	 * set total rows
	 *
	 * @param totalRows
	 */
	void setTotalRows(Integer totalRows);

	/**
	 * get total cols
	 * 
	 * @return
	 */
	Integer getTotalCols();

	/**
	 * set total cols
	 *
	 * @param totalCols
	 */
	void setTotalCols(Integer totalCols);

	/**
	 * get excel head
	 * 
	 * @return
	 */
	ExcelHeadProperty getExcelHeadProperty();

	/**
	 *
	 * @param clazz
	 * @param headOneRow
	 */
	void buildExcelHeadProperty(Class<? extends BaseRowModel> clazz, List<String> headOneRow);

	/**
	 * set current result
	 * 
	 * @param result
	 */
	void setCurrentRowAnalysisResult(Object result);

	/**
	 * get current result
	 * 
	 * @return get current result
	 */
	Object getCurrentRowAnalysisResult();

	/**
	 * Interrupt execution
	 */
	void interrupt();

	/**
	 * date use1904WindowDate
	 * 
	 * @return
	 */
	boolean use1904WindowDate();

	/**
	 * date use1904WindowDate
	 * 
	 * @param use1904WindowDate
	 */
	void setUse1904WindowDate(boolean use1904WindowDate);
}
