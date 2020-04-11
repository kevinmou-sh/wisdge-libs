package com.wisdge.ezcell.analysis;

import java.util.List;

import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.meta.Sheet;

public interface ExcelAnalyser {

    /**
     * parse one sheet
     *
     * @param sheetParam
     */
    void analysis(Sheet sheetParam);

    /**
     * parse all sheets
     */
    void analysis();
    
    /**
     * Get Context of Analyzer
     * @return AnalysisContext
     */
    AnalysisContext getContext();

    /**
     * get all sheet of workbook
     *
     * @return all sheets
     */
    List<Sheet> getSheets();

}
