package com.wisdge.ezcell;

import java.io.InputStream;
import java.util.Map;
import com.wisdge.ezcell.analysis.ExcelAnalyser;
import com.wisdge.ezcell.analysis.ExcelAnalyserImpl;
import com.wisdge.ezcell.annotation.ExcelTypeEnum;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.AnalysisEventListener;
import com.wisdge.ezcell.meta.Sheet;
import org.apache.poi.openxml4j.util.ZipSecureFile;

public class EzReader {
	private ExcelAnalyser analyser;

	/**
     * Create new reader
     *
     * @param inputStream
     * @param eventListener
     */
    public EzReader(InputStream inputStream, AnalysisEventListener<?> eventListener) {
    	this(inputStream, null, eventListener);
    }

	/**
     * Create new reader
     *
     * @param inputStream
     * @param customAttributes {@link AnalysisEventListener#invoke(Object, AnalysisContext) }AnalysisContext
     * @param eventListener
     */
    public EzReader(InputStream inputStream, Map<String, Object> customAttributes, AnalysisEventListener<?> eventListener) {
        ExcelTypeEnum excelTypeEnum = ExcelTypeEnum.valueOf(inputStream);
        analyser = new ExcelAnalyserImpl(inputStream, excelTypeEnum, customAttributes, eventListener);
        ZipSecureFile.setMinInflateRatio(-1.0d);
    }

    /**
     * Parse all sheet content by default
     */
    public void read() {
        analyser.analysis();
    }

    /**
     * Parse the specified sheet，SheetNo start from 1
     *
     * @param sheet Read sheet
     */
    public void read(Sheet sheet) {
        analyser.analysis(sheet);
    }

    public AnalysisContext getAnalysisContext() {
    	return analyser.getContext();
    }
}
