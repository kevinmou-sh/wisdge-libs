package com.wisdge.ezcell;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.SimpleEventListener;
import com.wisdge.ezcell.meta.Sheet;

public class EzCellFactory {
	public EzCellFactory() {
		throw new NullPointerException("Do not create instance of static factory");
	}

	/**
	 * 获取excel文件的预览信息
	 * @param inputStream Excel文件输入流
	 * @param previewSize 预览行数
	 * @return EzPreview对象
	 */
	public static EzPreview getPreview(InputStream inputStream, int previewSize) {
		Map<String, Object> attr = new HashMap<>();
		attr.put("previewSize", previewSize);
		
		SimpleEventListener listener = new SimpleEventListener();
		EzReader ezReader = new EzReader(inputStream, attr, listener);
        ezReader.read(new Sheet(1, 0));
        AnalysisContext content = ezReader.getAnalysisContext();
        return new EzPreview(content.getTotalRows(), content.getTotalCols(), listener.getData());
	}
}
