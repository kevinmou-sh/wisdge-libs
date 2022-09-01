package com.wisdge.ezcell;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.SimpleEventListener;
import com.wisdge.ezcell.meta.Sheet;
import com.wisdge.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.junit.Test;

@Slf4j
public class EzReaderTest {

	/**
	 * 07版本excel读数据量大于1千行，内部采用回调方法.
	 * @throws IOException 简单抛出异常，真实环境需要catch异常,同时在finally中关闭流
	 */
	// @Test
	public void saxReadListObjectsV2007() throws Exception {
        InputStream inputStream = new BufferedInputStream(FileUtils.openInputStream("/Users/kevinmou/QQ/保司账号导入--模板2.xlsx"));
		ZipSecureFile.setMinInflateRatio(-1.0d);
		EzPreview preview = EzCellFactory.getPreview(inputStream, 0);
		System.out.println("Total rows: " + preview.getTotalRows());
		System.out.println("Total cols: " + preview.getTotalCols());
		for (Object obj : preview.getElements()) {
			System.out.println(obj);
		}
		inputStream.close();
	}

	@Test
	public void testBigFile() throws Exception {
		Map<String, Object> attr = new HashMap<>();
		File file = new File("/Users/kevinmou/Documents/temp/test.xlsx");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		SimpleEventListener listener = new SimpleEventListener();
		EzReader ezReader = new EzReader(bis, attr, listener);
		ezReader.read(new Sheet(1, 0));
		AnalysisContext content = ezReader.getAnalysisContext();
		log.debug("Rows: {}", content.getTotalRows());
		bis.close();
		fis.close();
	}
}
