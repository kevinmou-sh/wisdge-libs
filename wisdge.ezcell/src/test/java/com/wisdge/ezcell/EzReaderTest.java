package com.wisdge.ezcell;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.wisdge.utils.FileUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;


public class EzReaderTest {

	/**
	 * 07版本excel读数据量大于1千行，内部采用回调方法.
	 *
	 * @throws IOException 简单抛出异常，真实环境需要catch异常,同时在finally中关闭流
	 */
	@org.junit.Test
	public void saxReadListObjectsV2007() throws Exception {
		InputStream inputStream = new BufferedInputStream(FileUtils.openInputStream("/Users/kevinmou/QQ/保司账号导入--模板2.xlsx"));
//        InputStream inputStream = new BufferedInputStream(FileUtils.openInputStream("/Users/kevinmou/Documents/2007.xlsx"));
		ZipSecureFile.setMinInflateRatio(-1.0d);
		EzPreview preview = EzCellFactory.getPreview(inputStream, 0);
		System.out.println("Total rows: " + preview.getTotalRows());
		System.out.println("Total cols: " + preview.getTotalCols());
		for (Object obj : preview.getElements()) {
			System.out.println(obj);
		}
		inputStream.close();
	}

	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage: jar wisdge.ezcell-1.0.0.jar com.wisdge.ezcell.EzReaderTest filepath <previewSize>");
			return;
		}

		int previewSize = 5;
		if (args.length > 1)
			previewSize = Integer.parseInt(args[1]);
		try (InputStream inputStream = new BufferedInputStream(FileUtils.openInputStream(args[0]))) {
			EzPreview preview = EzCellFactory.getPreview(inputStream, previewSize);
			System.out.println("Total rows: " + preview.getTotalRows());
			System.out.println("Total cols: " + preview.getTotalCols());
			for (Object obj : preview.getElements()) {
				System.out.println(obj);
			}
		}
	}

}
