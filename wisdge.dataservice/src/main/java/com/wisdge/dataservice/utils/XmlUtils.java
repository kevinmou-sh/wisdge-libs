package com.wisdge.dataservice.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.wisdge.dataservice.exceptions.IllegalFormatException;

public class XmlUtils {
	private static final String defaultCharestName = "UTF-8";

	/**
	 * 将<code>JSON</code>字符串解析为<code>JAVA</code>对象.
	 *
	 * @see JSonUtils#read(String)
	 * @param json
	 *            JSON字符串
	 * @return java对象
	 * @throws IllegalFormatException
	 * @throws IOException
	 * @see #parserXml(String)
	 */
	public static Object parserJson(String json) throws IOException, IllegalFormatException {
		return JSonUtils.read(json);
	}

	/**
	 * 从XML字符串中获取DOM4j的根元素
	 *
	 * @param buffer
	 *            XML的字符串
	 * @return Root element of Dom4j
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 * @throws Exception
	 * @see #parserJson(String)
	 */
	public static Element parserXml(String buffer) throws UnsupportedEncodingException, DocumentException {
		return parserXml(buffer, defaultCharestName);
	}

	/**
	 * 从XML字符串中获取DOM4j的根元素
	 *
	 * @param buffer
	 *            XML的字符串
	 * @param charsetName
	 *            XML字符集名称
	 * @return Root element of Dom4j
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 * @throws Exception
	 * @see #parserJson(String)
	 */
	public static Element parserXml(String buffer, String charsetName) throws UnsupportedEncodingException, DocumentException {
		if (StringUtils.isEmpty(buffer))
			return null;

		InputStream in = new ByteArrayInputStream(buffer.getBytes(charsetName));
		SAXReader reader = new SAXReader();
		return reader.read(in).getRootElement();
	}

	/**
	 * 从XML报文中获取对象值
	 *
	 * @param xmlStr
	 *            XML字符串
	 * @return XML解析出来的对象
	 * @throws Exception
	 */
	public static Object getObjectFromXML(String xmlStr) throws Exception {
		return getObjectFromXML(xmlStr, defaultCharestName);
	}

	/**
	 * 从XML报文中获取对象值
	 *
	 * @param xmlStr
	 *            XML字符串
	 * @param charsetName
	 *            XML字符集名称
	 * @return XML解析出来的对象
	 * @throws Exception
	 */
	public static Object getObjectFromXML(String xmlStr, String charsetName) throws Exception {
		if (xmlStr == null) {
			return null;
		}

		Element element = parserXml(xmlStr, charsetName);
		return getObjectFromDom(element);
	}

	/**
	 * 从Dom4J的element对象中获得值对象
	 *
	 * <pre>
	 * 对于可重复元素，方法会自动生成list对象。
	 * 但是对于少于2条重复元素的对象，如果标记有属性type为list，任然可以自动生成list对象。
	 * 如果元素内没有包含子元素，内容为单独的文本信息，则会将内容信息直接返回，例如：
	 * &lt;root&gt;this is test&lt;/root&gt;
	 * 	转换后获得：root = 'this is test'
	 * 如果元素包含其他属性，则文本信息内容会被赋到text字段值中，例如：
	 * &lt;root pro='1'&gt;this is test&lt;/root&gt;
	 * 	转换后获得： root.text = 'this is test'
	 * </pre>
	 *
	 * @param element
	 *            Element of dom4j
	 * @return Object值对象
	 */
	@SuppressWarnings("unchecked")
	public static Object getObjectFromDom(Element element) {
		Boolean isSeq = false;
		Map<String, Object> map = new HashMap<String, Object>();
		if (element == null) {
			return map;
		}

		for (Attribute attribute : element.attributes()) {
			map.put(attribute.getName(), attribute.getValue());
		}
		if (map.containsKey("type")) {
			isSeq = ((String) map.get("type")).equalsIgnoreCase("list");
		}

		List<Element> childList = element.elements();
		for (Element child : childList) {
			String name = child.getName();
			if (map.containsKey(name)) {
				Object obj = map.get(name);
				if (obj instanceof List<?>) {
					((List<Object>) obj).add(getObjectFromDom(child));
				} else if (obj instanceof Map<?, ?>) {
					List<Object> list = new ArrayList<Object>();
					list.add((obj));
					list.add(getObjectFromDom(child));
					map.put(name, list);
				} else {
					continue;
				}
			} else {
				if (isSeq) {
					List<Object> list = new ArrayList<Object>();
					list.add(getObjectFromDom(child));
					map.put(name, list);
				} else {
					map.put(name, getObjectFromDom(child));
				}
			}
		}

		if (childList.size() == 0) {
			if (map.size() == 0) {
				return element.getText();
			} else {
				if (!isSeq) {
					map.put("text", element.getText());
				}
			}
		}
		return map;
	}

	/**
	 * 读取webservice接口xml中的result片段
	 *
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object getWebServiceEntity(Element element) {
		List<Element> children = element.elements();
		if (children.size() == 0) {
			return element.getText();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		for (Element child : children) {
			String name = child.getName();
			if (map.containsKey(name)) {
				Object obj = map.get(name);
				if (obj instanceof List<?>) {
					((List<Object>) obj).add(getWebServiceEntity(child));
				} else {
					List<Object> list = new ArrayList<Object>();
					list.add((obj));
					list.add(getWebServiceEntity(child));
					map.put(name, list);
				}
			} else {
				map.put(name, getWebServiceEntity(child));
			}
		}
		return map;
	}

	/**
	 * 观察一个Map对象，以JSON对象格式输出到字符串
	 *
	 * @param map
	 *            Verbose target Map
	 * @return Format string
	 * @throws IOException
	 */
	public static String verboseMap(Map<?, ?> map) {
		return verboseMap(map, 0);
	}

	private static String verboseMap(Map<?, ?> map, int indit) {
		Iterator<?> iter = map.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		while (iter.hasNext()) {
			Entry<?, ?> e = (Entry<?, ?>) iter.next();
			Object key = e.getKey();
			Object value = e.getValue();
			sb.append(StringUtils.repeat("\t", indit + 1)).append(key == map ? "(this Map)" : key);
			sb.append(" : ");
			sb.append(value == map ? "(this Map)" : printValue(value, indit + 1));
			sb.append(StringUtils.repeat("\t", indit + 1)).append("\n");

		}
		sb.append(StringUtils.repeat("\t", indit)).append("}\n");
		return sb.toString();
	}

	private static String printValue(Object value, int indit) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return "\"" + value + "\"";
		} else if (value instanceof Map<?, ?>) {
			return verboseMap((Map<?, ?>) value, indit);
		} else if (value instanceof List<?>) {
			return verboseList((List<?>) value, indit);
		} else {
			return value.toString();
		}
	}

	private static String verboseList(List<?> list, int indit) {
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (Object item : list) {
			sb.append(StringUtils.repeat("\t", indit + 1));
			sb.append(printValue(item, indit + 1));
		}
		sb.append(StringUtils.repeat("\t", indit + 1)).append("]");
		return sb.toString();
	}

	public void test() throws Exception {
		String strXML = FileUtils.readFileToString(new File("d:/temp/test.xml"), "GBK");
		System.out.println(XmlUtils.getObjectFromXML(strXML, "GBK"));
	}
}
