package com.wisdge.web.filetypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Description: file types class Copyright:(c)2005 Wisdge.com
 *
 * @author Kevin MOU
 * @version 1.2
 */
@Slf4j
public class FileExt {
	private static java.util.Vector<Map<String, String>> extVT = null;

	private static synchronized void initialize() {
		if (extVT != null) {
			return;
		}

		extVT = new java.util.Vector<>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			InputStream in = FileExt.class.getClassLoader().getResourceAsStream("FileExtType.xml");
			Document doc = builder.parse(in);
			Element root = doc.getDocumentElement();

			// get attachment property
			NodeList nl = root.getElementsByTagName("suffix");
			for (int i = 0; i < nl.getLength(); i++) {
				Map<String, String> suffix = new HashMap<String, String>();
				suffix.put("ext", ((Element) nl.item(i)).getAttribute("ext"));
				suffix.put("image", ((Element) nl.item(i)).getAttribute("image"));
				suffix.put("contentType", ((Element) nl.item(i)).getAttribute("contentType"));
				extVT.add(suffix);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 根据文件后缀，取得相应的ContentType
	 *
	 * @param ext
	 *            文件后缀
	 * @return ContentType的String对象
	 */
	public static String getContentTypeByExt(String ext) {
		if (ext == null) {
			return "application/x-msdownload";
		}

		if (extVT == null) {
			initialize();
		}
		for (int i = 0; i < extVT.size(); i++) {
			Map<String, String> suffix = extVT.get(i);
			if (suffix.get("ext").equals(ext)) {
				return suffix.get("contentType");
			}
		}
		return "application/x-msdownload";
	}

	/**
	 * 根据文件名，取得相应的ContentType
	 *
	 * @param filename
	 *            文件名
	 * @return ContentType的String对象
	 */
	public static String getContentTypeByFilename(String filename) {
		if (filename == null) {
			return "application/x-msdownload";
		}
		return getContentTypeByExt(FilenameUtils.getExtension(filename));
	}

	/**
	 * 根据文件后缀，取得对应的LOGO图片
	 *
	 * @param ext String 文件名后缀
	 * @return byte[] LOGO图片
	 */
	public static byte[] getImgByExt(String ext) throws IOException {
		String imgFilename = "shb.gif";

		if (StringUtils.isNotEmpty(ext)) {
			if (extVT == null) {
				initialize();
			}
			for (int i = 0; i < extVT.size(); i++) {
				Map<String, String> suffix = extVT.get(i);
				if (suffix.get("ext").equals(ext)) {
					imgFilename = suffix.get("image");
				}
			}
		}

		try (InputStream in = FileExt.class.getClassLoader().getResourceAsStream("file-types-images/" + imgFilename)) {
			return IOUtils.toByteArray(in);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据文件名，取得对应的LOGO图片
	 *
	 * @param filename String 文件名
	 * @return byte[] LOGO图片
	 */
	public static byte[] getImgByFilename(String filename) throws IOException {
		return getImgByExt(FilenameUtils.getExtension(filename));
	}

}
