package com.wisdge.ezcell;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.AnalysisEventListener;
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
	public static EzPreview getPreview(InputStream inputStream, int previewSize) throws Exception {
		Map<String, Object> attr = new HashMap<>();
		attr.put("previewSize", previewSize);

		SimpleEventListener listener = new SimpleEventListener();
		EzReader ezReader = new EzReader(inputStream, attr, listener);
        ezReader.read(new Sheet(1, 0));
        AnalysisContext content = ezReader.getAnalysisContext();
        return new EzPreview(content.getTotalRows(), content.getTotalCols(), listener.getData());
	}

	public static EzSize read(InputStream inputStream, EzReaderProcessor processor) throws Exception {
		Map<String, Object> attr = new HashMap<>();
		attr.put("previewSize", 0);
		EzReader ezReader = new EzReader(inputStream, attr, new AnalysisEventListener<List<Object>>() {
			@Override
			public void invoke(List<Object> object, AnalysisContext context) {
				processor.process(object);
			}

			@Override
			public void doAfterAllAnalysed(AnalysisContext context) {

			}
		});
		ezReader.read(new Sheet(1, 0));
		AnalysisContext content = ezReader.getAnalysisContext();
		return new EzSize(content.getTotalRows(), content.getTotalCols());
	}
}
