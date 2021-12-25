package com.wisdge.ezcell.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.AnalysisEventListener;
import com.wisdge.ezcell.event.AnalysisEventRegisterCenter;
import com.wisdge.ezcell.meta.Sheet;

public abstract class BaseSaxAnalyser implements AnalysisEventRegisterCenter, ExcelAnalyser {

    protected AnalysisContext analysisContext;

	private LinkedHashMap<String, AnalysisEventListener> listeners = new LinkedHashMap<String, AnalysisEventListener>();

    /**
     * execute method
     */
    protected abstract void execute() throws Exception;


    @Override
    public void appendLister(String name, AnalysisEventListener<?> listener) {
        if (!listeners.containsKey(name)) {
            listeners.put(name, listener);
        }
    }

    @Override
    public void analysis(Sheet sheetParam) throws Exception {
        execute();
    }

    @Override
    public void analysis() throws Exception {
        execute();
    }

	@Override
    public void cleanAllListeners() {
        listeners = new LinkedHashMap<>();
    }

	@Override
    public void notifyListeners(List<Object> event) {
    	if (isEmptyList(event))
    		return;
    	
    	List<Object> clone = new ArrayList<>();
    	clone.addAll(event);
    	
        analysisContext.setCurrentRowAnalysisResult(clone);
        /** Parsing header content **/
        if (analysisContext.getCurrentRowNo() < analysisContext.getCurrentSheet().getHeadLineMun()) {
            if (analysisContext.getCurrentRowNo() <= analysisContext.getCurrentSheet().getHeadLineMun() - 1) {
                analysisContext.buildExcelHeadProperty(null, (List<String>)analysisContext.getCurrentRowAnalysisResult());
            }
        } else {
            /**  notify all event listeners **/
            for (@SuppressWarnings("rawtypes") Map.Entry<String, AnalysisEventListener> entry : listeners.entrySet()) {
                entry.getValue().invoke(analysisContext.getCurrentRowAnalysisResult(), analysisContext);
            }
        }
    }
    
    private boolean isEmptyList(List<Object> objects) {
    	for(Object obj : objects) {
    		if (obj != null)
    			return false;
    	}
    	return true;
    }
}
