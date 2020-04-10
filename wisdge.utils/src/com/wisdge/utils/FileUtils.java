/**
 * Description: File utility classes
 * Copyright: (c)2014 Wisdge.com
 * @author Kevin MOU
 * @version 1.14
 */
package com.wisdge.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtils extends org.apache.commons.io.FileUtils {

	/**
	 * 获得一个自增长序号的文件。<br/>
	 * 该方法通常用于对于已经存在的文件不采用覆盖的方式，而是自动增加序号在文件命中。
	 * 
	 * @param filepath
	 *            文件路径
	 * @return File
	 */
	public static File getNoDupFile(String filepath) {
		String path, basename, extension, filename;
		path = FilenameUtils.getFullPath(filepath);
		basename = FilenameUtils.getBaseName(filepath);
		extension = FilenameUtils.getExtension(filepath);
		int i = 0;
		File file = null;
		do {
			if (i > 0) {
				filename = basename + "(" + i + ")" + (StringUtils.isEmpty(extension) ? "" : ("." + extension));
			} else {
				filename = basename + (StringUtils.isEmpty(extension) ? "" : ("." + extension));
			}
			file = new File(path + filename);
			i++;
		} while (file.exists());
		return file;
	}
	
	public static String read(InputStream is) throws IOException {
		byte[] buffer = new byte[is.available()];
		IOUtils.read(is, buffer);
		return new String(buffer);
	}
	
	public static FileInputStream openInputStream(String filepath) throws Exception {
		File file = new File(filepath);
		if (file.exists() && !file.isDirectory() && file.canRead()) {
			return openInputStream(file);
		}
		throw new FileNotFoundException();
	}
}
