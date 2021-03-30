package com.wisdge.ezcell.event;

import java.util.ArrayList;
import java.util.List;
import com.wisdge.ezcell.context.AnalysisContext;

public class SimpleEventListener extends AnalysisEventListener<Object> {
    private List<Object> data = new ArrayList();

	@Override
	public void invoke(Object object, AnalysisContext context) {
		data.add(object);
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
	}

	public List<Object> getData() {
        return data;
    }
}
