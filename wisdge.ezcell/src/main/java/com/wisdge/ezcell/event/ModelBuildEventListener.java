package com.wisdge.ezcell.event;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;
import java.util.List;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.exception.ExcelGenerateException;
import com.wisdge.ezcell.meta.ExcelHeadProperty;
import com.wisdge.ezcell.utils.TypeUtil;

@Slf4j
public class ModelBuildEventListener extends AnalysisEventListener {

	@Override
	public void invoke(Object object, AnalysisContext context) {
		if (context.getExcelHeadProperty() != null && context.getExcelHeadProperty().getHeadClazz() != null) {
			try {
				Object resultModel = buildUserModel(context, (List<String>) object);
				context.setCurrentRowAnalysisResult(resultModel);
			} catch (Exception e) {
				throw new ExcelGenerateException(e);
			}
		} else {
			log.error("invoke", new NullPointerException("Null excel head property"));
		}
	}

	private Object buildUserModel(AnalysisContext context, List<String> stringList) throws Exception {
		ExcelHeadProperty excelHeadProperty = context.getExcelHeadProperty();
		Object resultModel = excelHeadProperty.getHeadClazz().newInstance();
		if (excelHeadProperty == null) {
			return resultModel;
		}
		BeanMap.create(resultModel).putAll(TypeUtil.getFieldValues(stringList, excelHeadProperty, context.use1904WindowDate()));
		return resultModel;
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {

	}
}
