package com.wisdge.ezcell.analysis;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.wisdge.ezcell.analysis.v03.XlsSaxAnalyser;
import com.wisdge.ezcell.analysis.v07.XlsxSaxAnalyser;
import com.wisdge.ezcell.annotation.ExcelTypeEnum;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.context.AnalysisContextImpl;
import com.wisdge.ezcell.event.AnalysisEventListener;
import com.wisdge.ezcell.event.ModelBuildEventListener;
import com.wisdge.ezcell.exception.ExcelAnalysisException;
import com.wisdge.ezcell.meta.Sheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelAnalyserImpl implements ExcelAnalyser {
	private AnalysisContext analysisContext;
	private BaseSaxAnalyser saxAnalyser;

	public ExcelAnalyserImpl(InputStream inputStream, ExcelTypeEnum excelTypeEnum, Map<String, Object> custom, AnalysisEventListener<?> eventListener) {
		analysisContext = new AnalysisContextImpl(inputStream, excelTypeEnum, custom, eventListener);
	}

	@Override
	public void analysis() throws Exception {
		BaseSaxAnalyser saxAnalyser = getSaxAnalyser();
		appendListeners(saxAnalyser);
		log.debug("With {}", saxAnalyser.getClass().getName());
		saxAnalyser.execute();
		analysisContext.getEventListener().doAfterAllAnalysed(analysisContext);
	}

	private BaseSaxAnalyser getSaxAnalyser() {
		if (saxAnalyser != null) {
			return this.saxAnalyser;
		}
		try {
			if (analysisContext.getExcelType() != null) {
				switch (analysisContext.getExcelType()) {
				case XLS:
					this.saxAnalyser = new XlsSaxAnalyser(analysisContext);
					break;
				case XLSX:
					this.saxAnalyser = new XlsxSaxAnalyser(analysisContext);
					break;
				}
			} else {
				try {
					this.saxAnalyser = new XlsxSaxAnalyser(analysisContext);
				} catch (Exception e) {
					if (! analysisContext.getInputStream().markSupported()) {
						throw new ExcelAnalysisException("Xls must be available markSupported,you can do like this <code> new BufferedInputStream(new FileInputStream(\"/xxxx\"))</code>");
					}
					this.saxAnalyser = new XlsSaxAnalyser(analysisContext);
				}
			}
		} catch (Exception e) {
			throw new ExcelAnalysisException("File type error，io must be available markSupported,you can do like this <code> new BufferedInputStream(new FileInputStream(\\\"/xxxx\\\"))</code> \"", e);
		}
		return this.saxAnalyser;
	}

	@Override
	public void analysis(Sheet sheetParam) throws Exception {
		analysisContext.setCurrentSheet(sheetParam);
		analysis();
	}

	@Override
	public List<Sheet> getSheets() throws Exception {
		BaseSaxAnalyser saxAnalyser = getSaxAnalyser();
		saxAnalyser.cleanAllListeners();
		return saxAnalyser.getSheets();
	}

	private void appendListeners(BaseSaxAnalyser saxAnalyser) {
		saxAnalyser.cleanAllListeners();
		if (analysisContext.getCurrentSheet() != null && analysisContext.getCurrentSheet().getClazz() != null) {
			saxAnalyser.appendLister("model_build_listener", new ModelBuildEventListener());
		}
		if (analysisContext.getEventListener() != null) {
			saxAnalyser.appendLister("user_define_listener", analysisContext.getEventListener());
		}
	}

	@Override
	public AnalysisContext getContext() {
		return this.analysisContext;
	}

}
